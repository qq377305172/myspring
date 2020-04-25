package com.jing.service.impl;

import com.example.demo.dao.OmsCartItemDao;
import com.example.demo.entity.OmsCartItem;
import com.example.demo.service.CartService;
import com.example.demo.util.JsonUtil;
import com.example.demo.util.RedisUtil;
import com.jfinal.kit.StrKit;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Admin
 * @title: CartServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/3/7 13:39
 */
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Resource
    private OmsCartItemDao omsCartItemDao;
    @Resource
    private RedisUtil redisUtil;

    private String getCartCacheKey(Long memberId) {
        return "user:" + memberId + ":cart";
    }

    @Override
    public OmsCartItem getCartByMemberIdAndSkuId(Long memberId, Long skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        return omsCartItemDao.queryOne(omsCartItem);
    }

    @Override
    public int updateCart(OmsCartItem omsCartItemFromDb) {
        return omsCartItemDao.update(omsCartItemFromDb);
    }

    @Override
    public int addCart(OmsCartItem omsCartItem) {
        return omsCartItemDao.insert(omsCartItem);
    }

    @Override
    public void sync(Long memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemDao.queryAll(omsCartItem);
        if (CollectionUtils.isEmpty(omsCartItems)) {
            return;
        }
        Jedis jedis = redisUtil.getJedis();
        Map<String, String> value = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
            value.put(cartItem.getProductSkuId().toString(), JsonUtil.obj2Json(cartItem));
        }
        String cartCacheKey = getCartCacheKey(memberId);
        jedis.del(cartCacheKey);
        jedis.hmset(cartCacheKey, value);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> getCartByMemberId(Long memberId) {
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        Jedis jedis = null;
        try {
            String cartCacheKey = getCartCacheKey(memberId);
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals(cartCacheKey);
            if (!CollectionUtils.isEmpty(hvals)) {
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JsonUtil.json2Obj(hval, OmsCartItem.class);
                    omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                    omsCartItemList.add(omsCartItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return omsCartItemList;
    }

    @Override
    public void checkCart(Long memberId, long skuId, int isChecked) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        int update = omsCartItemDao.updateCheckedStatus(omsCartItem);
        if (update == 1) {
            //更新成功,刷新缓存
            sync(memberId);
        }
    }

    @Override
    public int deleteBoughtProducts(Long memberId, List<OmsCartItem> boughtProductIdList) {
        Jedis jedis = null;
        try {
            String cartCacheKey = getCartCacheKey(memberId);
            jedis = redisUtil.getJedis();
            //同步缓存
            String cartListStrInCache = jedis.get(cartCacheKey);
            if (StrKit.isBlank(cartListStrInCache)) {
                return 1;
            }
            Map<String, String> map = JsonUtil.json2MapStr(cartListStrInCache);
            String cartCacheValueObj = map.get(String.valueOf(memberId));
            if (StrKit.isBlank(cartCacheValueObj)) {
                return 1;
            }

            Map<Long, OmsCartItem> idOmsCartItemMapping = new HashMap<>();
            for (OmsCartItem omsCartItem : boughtProductIdList) {
                idOmsCartItemMapping.put(omsCartItem.getId(), omsCartItem);
            }

            List<OmsCartItem> pmsSkuInfoList = JsonUtil.json2list(cartCacheValueObj, OmsCartItem.class);
            ListIterator<OmsCartItem> pmsSkuInfoListIterator = pmsSkuInfoList.listIterator();
            while (pmsSkuInfoListIterator.hasNext()) {
                OmsCartItem next = pmsSkuInfoListIterator.next();
                Long id = next.getId();
                OmsCartItem omsCartItem = idOmsCartItemMapping.get(id);
                if (null == omsCartItem)
                    continue;
                if (omsCartItem.getQuantity().subtract(next.getQuantity()) == null) {
                    pmsSkuInfoListIterator.remove();
                } else {
                    next.setQuantity(omsCartItem.getQuantity().subtract(next.getQuantity()));
                }
            }
            pmsSkuInfoList.removeIf(next -> boughtProductIdList.contains(next.getId()));
            if (pmsSkuInfoList.isEmpty()) {
                return 1;
            }
            map.put(String.valueOf(memberId), JsonUtil.collection2Json(pmsSkuInfoList));
            jedis.hmset(cartCacheKey, map);
            //同步数据库
            return 1;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}

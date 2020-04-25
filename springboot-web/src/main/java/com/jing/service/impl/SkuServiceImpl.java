package com.jing.service.impl;

import com.example.demo.dao.PmsSkuAttrValueDao;
import com.example.demo.dao.PmsSkuImageDao;
import com.example.demo.dao.PmsSkuInfoDao;
import com.example.demo.dao.PmsSkuSaleAttrValueDao;
import com.example.demo.entity.PmsSkuAttrValue;
import com.example.demo.entity.PmsSkuImage;
import com.example.demo.entity.PmsSkuInfo;
import com.example.demo.entity.PmsSkuSaleAttrValue;
import com.example.demo.service.SkuService;
import com.example.demo.util.JsonUtil;
import com.example.demo.util.RedisUtil;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @author Admin
 * @title: SkuServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/2/2610:29
 */
@Service("skuService")
public class SkuServiceImpl implements SkuService {
    @Resource
    private PmsSkuInfoDao pmsSkuInfoDao;
    @Resource
    private PmsSkuImageDao pmsSkuImageDao;
    @Resource
    private PmsSkuAttrValueDao pmsSkuAttrValueDao;
    @Resource
    private PmsSkuSaleAttrValueDao pmsSkuSaleAttrValueDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        if (null == pmsSkuInfo.getId()) {
            //新增
            return addSku(pmsSkuInfo);
        } else {
            //修改
            return updateSku(pmsSkuInfo);
        }
    }

    @Resource
    RedisUtil redisUtil;

    @Override
    public PmsSkuInfo getSkuInfo(Long skuId) {
        Prop prop = PropKit.use("redis_constants.properties");
        String prefix = prop.get("sku_info_prefix");
        String suffix = prop.get("sku_info_suffix");
        String lockSuffix = prop.get("sku_lock_suffix");
        String key = prefix + skuId + suffix;
        Jedis jedis = redisUtil.getJedis();
        String jsonStr = jedis.get(key);
        if (!StringUtils.isEmpty(jsonStr)) {
            jedis.close();
            return JsonUtil.json2Obj(jsonStr, PmsSkuInfo.class);
        }
        PmsSkuInfo pmsSkuInfo;
        //加锁的key
        String lockKey = prefix + lockSuffix + skuId + lockSuffix;
        //分布式锁的过期时间,在此时间内,其他线程无法成功设置缓存
        Integer lockCacheTime = prop.getInt("lock_cache_time");
        //设置redis自带的分布式锁
        UUID uuid = UUID.randomUUID();
        String status = jedis.set(lockKey, uuid.toString(), "nx", "px", lockCacheTime);
        if ("OK".equals(status)) {
            //设置成功,有权限在10秒内访问数据库
            pmsSkuInfo = getSkuInfoFromDB(skuId);
            if (null != pmsSkuInfo) {
                //从数据库中查询到了该数据,将查询结果存入缓存
                jedis.set(key, JsonUtil.obj2Json(pmsSkuInfo));
            } else {
                //数据库中不存在该数据
                //防止缓存穿透,将null或者空字符串存入缓存
                Integer cacheTime = prop.getInt("cache_time");
                jedis.setex(key, cacheTime, "");
            }
            //在访问mysql后,将mysql的分布式锁释放
            if (uuid.toString().equals(jedis.get("lockKey"))) {
                jedis.del(lockKey);
            }
        } else {
            //设置失败,自旋(在该线程睡眠几秒后重新访问本方法)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getSkuInfo(skuId);
        }
        jedis.close();
        return pmsSkuInfo;
    }

    private PmsSkuInfo getSkuInfoFromDB(Long skuId) {
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoDao.queryById(skuId);
        if (null == pmsSkuInfo) {
            return null;
        }
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageDao.queryAll(pmsSkuImage);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);

        PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
        pmsSkuAttrValue.setSkuId(skuId);
        List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueDao.queryAll(pmsSkuAttrValue);
        pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);

        PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
        pmsSkuSaleAttrValue.setSkuId(skuId);
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueDao.queryAll(pmsSkuSaleAttrValue);
        pmsSkuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);
        return pmsSkuInfo;
    }

    private boolean updateSku(PmsSkuInfo pmsSkuInfo) {
        return false;

    }

    private boolean addSku(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        if (!CollectionUtils.isEmpty(pmsSkuInfo.getSkuImageList()) && null != pmsSkuInfo.getSkuImageList().get(0)) {
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }
        pmsSkuInfoDao.insert(pmsSkuInfo);
        Long id = pmsSkuInfo.getId();
        if (null == id || id <= 0) {
            throw new RuntimeException("回滚");
        }
        //保存图片
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            for (PmsSkuImage pmsSkuImage : skuImageList) {
                pmsSkuImage.setSkuId(id);
                int insert = pmsSkuImageDao.insert(pmsSkuImage);
                if (insert == 0) {
                    throw new RuntimeException("回滚");
                }
            }
        }

        //保存sku属性值
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (PmsSkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(id);
                int insert = pmsSkuAttrValueDao.insert(skuAttrValue);
                if (insert == 0) {
                    throw new RuntimeException("回滚");
                }
            }
        }
        //保存sku销售属性值
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                pmsSkuSaleAttrValue.setSkuId(id);
                int insert = pmsSkuSaleAttrValueDao.insert(pmsSkuSaleAttrValue);
                if (insert == 0) {
                    throw new RuntimeException("回滚");
                }
            }
        }
        return true;
    }
}

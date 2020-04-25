package com.jing.service.impl;

import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.service.AttrService;
import com.example.demo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Whyn
 * @date 2020/2/20 18:28
 */
@Service("attrService")
public class AttrServiceImpl implements AttrService {
    Logger logger = LoggerFactory.getLogger(AttrServiceImpl.class);
    @Resource
    PmsBaseAttrInfoDao pmsBaseAttrInfoDao;
    @Resource
    PmsBaseSaleAttrDao pmsBaseSaleAttrDao;
    @Resource
    PmsBaseAttrValueDao pmsBaseAttrValueDao;
    @Resource
    PmsProductSaleAttrDao pmsProductSaleAttrDao;
    @Resource
    private PmsProductImageDao pmsProductImageDao;
    @Resource
    private PmsProductSaleAttrValueDao pmsProductSaleAttrValueDao;
    @Resource
    private PmsSkuInfoDao pmsSkuInfoDao;
    @Resource
    private PmsSkuAttrValueDao pmsSkuAttrValueDao;

    @Override
    public List<PmsBaseAttrInfo> queryAllByCatalog(Long catalog3Id) {
        return pmsBaseAttrInfoDao.queryAllByCatalog(catalog3Id);
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrDao.queryAll(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        Long id = pmsBaseAttrInfo.getId();
        if (null == id) {
            //新增
            return insertInfo(pmsBaseAttrInfo);
        } else {
//            修改
            return updateInfo(pmsBaseAttrInfo);
        }
    }

    private int updateInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        int result = pmsBaseAttrInfoDao.update(pmsBaseAttrInfo);
        if (result != 1) {
            logger.error("更新属性失败,执行回滚操作");
            throw new RuntimeException("更新属性失败,执行回滚操作");
        }
        //先删除该属性信息关联的属性值
        PmsBaseAttrValue deleteValues = new PmsBaseAttrValue();
        deleteValues.setAttrId(pmsBaseAttrInfo.getId());
        pmsBaseAttrValueDao.delete(deleteValues);
        //再插入新的属性值
        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
        if (!StringUtils.isEmpty(attrValueList)) {
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                int insertCount = pmsBaseAttrValueDao.insert(pmsBaseAttrValue);
                result = insertCount == 1 ? 1 : 0;
            }
        }
        return result;

    }

    private int insertInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        pmsBaseAttrInfoDao.insert(pmsBaseAttrInfo);
        Long id = pmsBaseAttrInfo.getId();
        if (null == id || id <= 0) {
            logger.error("插入属性名称失败,执行回滚操作");
            throw new RuntimeException();
        }
        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)) {
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(id);
                pmsBaseAttrValueDao.insert(pmsBaseAttrValue);
            }
        }
        return 1;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueListByAttrId(Long attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return pmsBaseAttrValueDao.queryAll(pmsBaseAttrValue);
    }

    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrListBySpuId(Long spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrDao.queryAll(pmsProductSaleAttr);
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueDao.queryAll(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> getSpuImageListBySpuId(Long spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        return pmsProductImageDao.queryAll(pmsProductImage);
    }

    @Override
    public List<PmsProductSaleAttr> getAttrInfoBySkuId(Long skuId, Long productId) {
        return pmsProductSaleAttrDao.querySaleAttrByChecked(skuId, productId);
    }

//    @Resource
//    RedisUtil redisUtil;

//    @Override
//    public String getSkuSaleAttrValueInfos(Long productId) {
//        Prop prop = PropKit.use("redis_constants.properties");
//        String sku_attr_key_prefix = prop.get("sku_attr_key_prefix");
//        String sku_attr_key_suffix = prop.get("sku_attr_key_suffix");
//        Jedis jedis = redisUtil.getJedis();
//        String s = jedis.get("");
//        if (StringUtils.isEmpty(s)) {
//            String skuSaleAttrValueInfosFromDB = getSkuSaleAttrValueInfosFromDB(productId);
//            jedis.set("", skuSaleAttrValueInfosFromDB);
//            return skuSaleAttrValueInfosFromDB;
//        }
//        jedis.close();
//        return s;
//    }

    @Override
    public String getSkuSaleAttrValueInfos(Long productId) {
        List<PmsSkuInfo> skuSaleAttrValueInfos = pmsSkuInfoDao.getSkuSaleAttrValueInfos(productId);
        Map<String, Long> valuesSkuMapping = new HashMap<>();
        for (PmsSkuInfo skuInfo : skuSaleAttrValueInfos) {
            Long value = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            List<Long> keyList = new ArrayList<>();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                keyList.add(pmsSkuSaleAttrValue.getSaleAttrValueId());
            }
            valuesSkuMapping.put(org.apache.commons.lang.StringUtils.join(keyList, "|"), value);
        }
        return JsonUtil.map2Json(valuesSkuMapping);
    }

    @Override
    public List<PmsSkuInfo> getAllSkuInfo() {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoDao.queryAll(null);
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            Long id = pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(id);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueDao.queryAll(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfoList;
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrinfosByValueId(String sqlIn) {
        return pmsBaseAttrInfoDao.getAttrinfosByValueId(sqlIn);
    }

    @Override
    public PmsSkuInfo getSkuById(Long skuId) {
        return pmsSkuInfoDao.queryById(skuId);
    }
}

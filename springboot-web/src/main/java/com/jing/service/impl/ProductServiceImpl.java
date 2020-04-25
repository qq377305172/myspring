package com.jing.service.impl;

import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.service.ProductService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Admin
 */
@Service
public class ProductServiceImpl implements ProductService {
    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Resource
    PmsProductInfoDao pmsProductInfoDao;
    @Resource
    PmsProductSaleAttrDao pmsProductSaleAttrDao;
    @Resource
    PmsProductSaleAttrValueDao pmsProductSaleAttrValueDao;
    @Resource
    PmsProductImageDao pmsProductImageDao;
    @Resource
    private PmsSkuInfoDao pmsSkuInfoDao;

    @Override
    public List<PmsProductInfo> queryAllByCatalog(Integer catalog3Id) {
        return pmsProductInfoDao.queryAllByCatalog(catalog3Id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveSpuInfo(PmsProductInfo pmsProductInfo) {
        Long id = pmsProductInfo.getId();
        if (null == id) {
            //新增
            return insertSpuInfo(pmsProductInfo);
        } else {
            //修改
            return updateSpuInfo(pmsProductInfo);
        }
    }

    @Override
    public List<PmsSkuInfo> listSkuInfoBySkuIds(List<Long> skuIds) {
        return pmsSkuInfoDao.queryByIds(StringUtils.join(skuIds, ","));
    }


    private int updateSpuInfo(PmsProductInfo pmsProductInfo) {
        //1修改商品信息
        int updateCount = pmsProductInfoDao.update(pmsProductInfo);
        if (updateCount <= 0) {
            return 0;
        }
        //2修改商品销售信息
        updateSaleAttr(pmsProductInfo);
        //3修改商品图片信息
        updateProductImageInfo(pmsProductInfo);
        return 1;
    }

    private void updateProductImageInfo(PmsProductInfo pmsProductInfo) {
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        if (CollectionUtils.isEmpty(spuImageList)) {
            return;
        }
        for (PmsProductImage pmsProductImage : spuImageList) {
            int updateCount = pmsProductImageDao.update(pmsProductImage);
            if (updateCount <= 0) {
                //商品图片信息修改失败,执行回滚
                logger.error("商品图片信息修改失败,执行回滚操作");
                throw new RuntimeException("商品图片信息修改失败,执行回滚操作");
            }
        }
    }

    private void updateSaleAttr(PmsProductInfo pmsProductInfo) {
        if (CollectionUtils.isEmpty(pmsProductInfo.getSpuSaleAttrList())) {
            return;
        }
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductInfo.getSpuSaleAttrList()) {
            //先修改销售属性下的销售属性值信息
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                    int updateCount = pmsProductSaleAttrValueDao.update(pmsProductSaleAttrValue);
                    if (updateCount <= 0) {
                        //商品销售信息值修改失败,执行回滚
                        logger.error("商品销售信息值修改失败,执行回滚操作");
                        throw new RuntimeException("商品销售信息值修改失败,执行回滚操作");
                    }
                }
            }
            int updateCount = pmsProductSaleAttrDao.update(pmsProductSaleAttr);
            if (updateCount <= 0) {
                //商品销售信息值修改失败,执行回滚
                logger.error("商品销售信息修改失败,执行回滚操作");
                throw new RuntimeException("商品销售信息修改失败,执行回滚操作");
            }
        }

    }

    private int insertSpuInfo(PmsProductInfo pmsProductInfo) {
        //先插入商品信息,获取插入后的id
        pmsProductInfoDao.insert(pmsProductInfo);
        Long id = pmsProductInfo.getId();
        if (null == id || id <= 0) {
            return 0;
        }
        //要保存的销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
                pmsProductSaleAttr.setProductId(id);
                insertSaleAttr(pmsProductSaleAttr);
            }
        }
        //要保存的图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            for (PmsProductImage pmsProductImage : spuImageList) {
                pmsProductImage.setProductId(id);
                int insertCount = pmsProductImageDao.insert(pmsProductImage);
                if (insertCount <= 0) {
                    //商品图片信息插入失败,执行回滚
                    logger.error("商品图片信息插入失败,执行回滚操作");
                    throw new RuntimeException("商品图片信息插入失败,执行回滚操作");
                }
            }
        }
        return 1;
    }

    private void insertSaleAttr(PmsProductSaleAttr pmsProductSaleAttr) {
        pmsProductSaleAttrDao.insert(pmsProductSaleAttr);
        Long id = pmsProductSaleAttr.getId();
        if (null == id || id <= 0) {
            //商品销售属性插入失败,执行回滚
            logger.error("商品销售属性插入失败,执行回滚操作");
            throw new RuntimeException("商品销售属性插入失败,执行回滚操作");
        }
        List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setSaleAttrId(id);
                pmsProductSaleAttrValue.setProductId(pmsProductSaleAttr.getProductId());
                int insertCount = pmsProductSaleAttrValueDao.insert(pmsProductSaleAttrValue);
                if (insertCount <= 0) {
                    //销售属性值插入失败,执行回滚操作
                    logger.error("销售属性值插入失败,执行回滚操作");
                    throw new RuntimeException("销售属性值插入失败,执行回滚操作");
                }
            }
        }
    }
}

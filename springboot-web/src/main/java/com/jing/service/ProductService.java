package com.jing.service;

import com.example.demo.entity.PmsProductInfo;
import com.example.demo.entity.PmsSkuInfo;

import java.util.List;

/**
 * @author Whyn
 * @date 2020/2/20 18:08
 */
public interface ProductService {
    List<PmsProductInfo> queryAllByCatalog(Integer catalog3Id);

    int saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsSkuInfo> listSkuInfoBySkuIds(List<Long> skuIds);
}

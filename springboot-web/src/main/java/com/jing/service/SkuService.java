package com.jing.service;

import com.example.demo.entity.PmsSkuInfo;

/**
 * @author Admin
 * @title: SkuService
 * @projectName demo
 * @description: TODO
 * @date 2020/2/2610:28
 */
public interface SkuService {
    boolean saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfo(Long skuId);
}


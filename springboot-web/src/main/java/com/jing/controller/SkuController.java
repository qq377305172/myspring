package com.jing.controller;

import com.example.demo.entity.PmsSkuInfo;
import com.example.demo.service.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Whyn
 * @date 2020/2/26 10:00
 */
@RestController
@CrossOrigin
public class SkuController {
    @Resource
    private SkuService skuService;

    /**
     * 保存sku信息
     */
    @PostMapping("/saveSkuInfo")
    public boolean saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        return skuService.saveSkuInfo(pmsSkuInfo);
    }
}

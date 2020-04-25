package com.jing.controller;

import com.example.demo.entity.PmsProductSaleAttr;
import com.example.demo.entity.PmsSkuInfo;
import com.example.demo.service.AttrService;
import com.example.demo.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Admin
 * @title: IndexController
 * @projectName demo
 * @description: TODO
 * @date 2020/2/26 16:51
 */
@Controller
public class ItemController {
    @Resource
    private SkuService skuService;
    @Resource
    private AttrService attrService;

    @GetMapping("/{skuId}.html")
    public ModelAndView item(@PathVariable Long skuId, ModelAndView modelAndView) {
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfo(skuId);
        modelAndView.addObject("skuInfo", pmsSkuInfo);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = attrService.getAttrInfoBySkuId(pmsSkuInfo.getId(), pmsSkuInfo.getProductId());
        modelAndView.addObject("spuSaleAttrListCheckBySku", pmsProductSaleAttrList);
        String valuesSkuStr = attrService.getSkuSaleAttrValueInfos(pmsSkuInfo.getProductId());
        modelAndView.addObject("valuesSkuJsonStr", valuesSkuStr);
        modelAndView.setViewName("item");
        return modelAndView;
    }

}

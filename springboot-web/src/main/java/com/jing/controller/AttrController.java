package com.jing.controller;

import com.example.demo.entity.*;
import com.example.demo.service.AttrService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Whyn
 * @date 2020/2/20 18:17
 */
@RestController
@CrossOrigin
public class AttrController {
    @Resource
    private AttrService attrService;

    /**
     * 属性信息列表
     *
     * @param catalog3Id
     * @return
     */
    @GetMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(@RequestParam("catalog3Id") Long catalog3Id) {
        return attrService.queryAllByCatalog(catalog3Id);
    }

    /**
     * @return
     */
    @PostMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return attrService.baseSaleAttrList();
    }

    /**
     * 保存属性信息
     *
     * @param pmsBaseAttrInfo
     * @return
     */
    @Valid
    @PostMapping("/saveAttrInfo")
    public int saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        return attrService.saveAttrInfo(pmsBaseAttrInfo);
    }

    /**
     * 根据销售属性获取销售属性值
     *
     * @param attrId
     * @return
     */
    @PostMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId) {
        return attrService.getAttrValueListByAttrId(attrId);
    }

    /**
     * 获取商品销售属性
     */
    @GetMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(Long spuId) {
        return attrService.getSpuSaleAttrListBySpuId(spuId);
    }
    /**
     * 获取商品图片信息
     */
    @GetMapping("spuImageList")
    public List<PmsProductImage> spuImageList(Long spuId){
        return attrService.getSpuImageListBySpuId(spuId);

    }
}

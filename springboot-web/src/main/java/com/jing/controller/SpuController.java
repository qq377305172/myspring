package com.jing.controller;

import com.example.demo.entity.PmsProductInfo;
import com.example.demo.service.ProductService;
import com.example.demo.util.FdfsUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Whyn
 * @date 2020/2/22 16:24
 */
@CrossOrigin
@RestController
public class SpuController {
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;


    /**
     * 根据三级分类获取商品列表
     *
     * @return JsonResult
     */
    @ApiOperation("根据三级分类获取商品列表")
    @GetMapping("/spuList")
    public List<PmsProductInfo> queryAllByCatalog(Integer catalog3Id) {
        return this.productService.queryAllByCatalog(catalog3Id);
    }

    /**
     * 新增商品spu属性
     *
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public int saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        return productService.saveSpuInfo(pmsProductInfo);
    }

    /**
     * 图片上传
     */
    @PostMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        return FdfsUtil.uploadImage(file);
    }
}

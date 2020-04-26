//package com.jing.controller;
//
//import com.jing.service.CatalogService;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @author Whyn
// * @date 2020/2/20 11:28
// */
//@RestController
//@CrossOrigin
//public class CatalogController {
//    @Resource(name = "catalogService")
//    CatalogService catalogService;
//
//    /**
//     * 获取一级分类
//     */
//    @PostMapping("/getCatalog1")
//    public List<PmsBaseCatalog1> getCatalog1() {
//        return this.catalogService.getCatalog1();
//    }
//
//    /**
//     * 根据一级分类查询三级分类
//     */
//    @PostMapping("/getCatalog2")
//    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
//        return this.catalogService.getCatalog2(catalog1Id);
//    }
//
//    /**
//     * 根据二级分类查询三级分类
//     */
//    @PostMapping("/getCatalog3")
//    public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id) {
//        return this.catalogService.getCatalog3(catalog2Id);
//    }
//}

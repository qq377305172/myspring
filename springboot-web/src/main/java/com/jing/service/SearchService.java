package com.jing.service;

import com.example.demo.controller.vo.param.PmsSkuSearchParam;

import java.util.Map;

/**
 * @author Admin
 * @title: SearchService
 * @projectName demo
 * @description: TODO
 * @date 2020/3/1 14:31
 */
public interface SearchService {
    Map<String, Object> list(PmsSkuSearchParam pmsSkuSearchParam);

    String getUrlParam(PmsSkuSearchParam pmsSkuSearchParam, Long delValueId);

    String getUrlParamForCrumb(PmsSkuSearchParam pmsSkuSearchParam);
}

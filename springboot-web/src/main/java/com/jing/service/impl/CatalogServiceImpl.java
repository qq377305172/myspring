package com.jing.service.impl;

import com.example.demo.dao.PmsBaseCatalog1Dao;
import com.example.demo.dao.PmsBaseCatalog2Dao;
import com.example.demo.dao.PmsBaseCatalog3Dao;
import com.example.demo.entity.PmsBaseCatalog1;
import com.example.demo.entity.PmsBaseCatalog2;
import com.example.demo.entity.PmsBaseCatalog3;
import com.example.demo.service.CatalogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Whyn
 * @date 2020/2/20 18:02
 */
@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    @Resource
    PmsBaseCatalog1Dao pmsBaseCatalog1Dao;
    @Resource
    PmsBaseCatalog2Dao pmsBaseCatalog2Dao;
    @Resource
    PmsBaseCatalog3Dao pmsBaseCatalog3Dao;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Dao.queryAll(null);
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(int catalog1Id) {
        return pmsBaseCatalog2Dao.queryByParent(catalog1Id);
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(int catalog2Id) {
        return pmsBaseCatalog3Dao.queryByParent(catalog2Id);
    }
}

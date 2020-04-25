package com.jing.service;

import com.example.demo.entity.PmsBaseCatalog1;
import com.example.demo.entity.PmsBaseCatalog2;
import com.example.demo.entity.PmsBaseCatalog3;

import java.util.List;

/**
 * @author Whyn
 * @date 2020/2/20 18:02
 */
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();
    List<PmsBaseCatalog2> getCatalog2(int catalog1Id);
    List<PmsBaseCatalog3> getCatalog3(int catalog2Id);
}

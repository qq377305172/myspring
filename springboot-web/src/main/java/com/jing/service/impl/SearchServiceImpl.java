package com.jing.service.impl;

import com.example.demo.controller.vo.param.PmsSkuSearchParam;
import com.example.demo.entity.PmsSkuAttrValue;
import com.example.demo.entity.search.PmsSearchSkuInfo;
import com.example.demo.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author Admin
 * @title: SearchServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/3/1 14:33
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {
    @Resource
    private JestClient jestClient;

    @Override
    public Map<String, Object> list(PmsSkuSearchParam pmsSkuSearchParam) {
        String builder = getSearchSourceBuilder(pmsSkuSearchParam);
        Search search = new Search.Builder(builder).addIndex("gmall").addType("PmsSkuInfo").build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == searchResult) {
            return null;
        }
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        Set<Long> attrvalueids = new HashSet<>();
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            if (!CollectionUtils.isEmpty(highlight)) {
                String skuName = highlight.get("skuName").get(0);
                source.setSkuName(skuName);
            }
            List<PmsSkuAttrValue> skuAttrValueList = source.getSkuAttrValueList();
            if (null != skuAttrValueList) {
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    attrvalueids.add(pmsSkuAttrValue.getValueId());
                }
            }
            pmsSearchSkuInfoList.add(source);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", pmsSearchSkuInfoList);
        String sqlIn = attrvalueids.size() == 0 ? "" : org.apache.commons.lang.StringUtils.join(attrvalueids, ",");
        map.put("set", sqlIn);
        return map;
    }

    @Override
    public String getUrlParam(PmsSkuSearchParam pmsSkuSearchParam, Long delValueId) {
        StringBuilder builder = new StringBuilder();
        String keyword = pmsSkuSearchParam.getKeyword();
        Long catalog3Id = pmsSkuSearchParam.getCatalog3Id();
        Long[] valueIdArr = pmsSkuSearchParam.getValueId();
        if (!StringUtils.isEmpty(keyword)) {
            builder.append("&keyword=").append(keyword);
        }
        if (!StringUtils.isEmpty(catalog3Id)) {
            builder.append("&catalog3Id=").append(catalog3Id);
        }
        if (null != valueIdArr && valueIdArr.length >= 1) {
            for (Long valueId : valueIdArr) {
                if (null != delValueId) {
                    if (!delValueId.equals(valueId)) {
                        builder.append("&valueId=").append(valueId);
                    }
                } else {
                    builder.append("&valueId=").append(valueId);
                }
            }
        }
        if (builder.length() >= 1) {
            return builder.substring(1);
        }
        return "";
    }

    @Override
    public String getUrlParamForCrumb(PmsSkuSearchParam pmsSkuSearchParam) {
        return null;
    }

    private String getSearchSourceBuilder(PmsSkuSearchParam pmsSkuSearchParam) {
        //jest的dsl查询工具
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //filter
        if (null != pmsSkuSearchParam.getCatalog3Id()) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", pmsSkuSearchParam.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        Long[] valueIdArr = pmsSkuSearchParam.getValueId();
        if (null != valueIdArr && valueIdArr.length >= 1) {
            for (Long valueId : valueIdArr) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //must
        if (!StringUtils.isEmpty(pmsSkuSearchParam.getKeyword())) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", pmsSkuSearchParam.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //查询条件
        builder.query(boolQueryBuilder);
        //分页
        builder.from((pmsSkuSearchParam.getPageNumber() - 1) * pmsSkuSearchParam.getPageSize());
        builder.size(pmsSkuSearchParam.getPageSize());
        //排序
        builder.sort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        builder.highlight(highlightBuilder);
        //聚合
//        TermsBuilder field = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList");
        return builder.toString();
    }
}

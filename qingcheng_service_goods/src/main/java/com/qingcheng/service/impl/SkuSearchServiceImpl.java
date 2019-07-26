package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuSearchServiceImpl implements SkuSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpecMapper specMapper;

    @Override
    public Map search(Map<String, String> searchMap) {
        // 1.封装查询请求
        SearchRequest searchRequest = new SearchRequest("sku");
        // 设置查询的类型
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 布尔查询构建器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 1.1关键字搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
        boolQueryBuilder.must(matchQueryBuilder);

        // 1.2聚合查询(商品分类)
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        // 1.2.1商品分类过滤
        if (searchMap.get("category") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 1.3品牌过滤
        if (searchMap.get("brand") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 1.4规格对象过滤
        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec.")) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(key + ".keyword", searchMap.get(key));
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // 1.5 价格过滤
        if (searchMap.get("price") != null) {
            String[] price = searchMap.get("price").split("-");
            if (!price[0].equals("0")) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(price[0] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
            if (!price[1].equals("*")) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lte(price[1] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 1.6 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<font style='color:red'>").postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 1.7分页
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Integer pageSize = 30;
        Integer findIndex = (pageNo - 1) * pageSize;
        searchSourceBuilder.from(findIndex).size(pageSize);

        // 1.8排序
        String sort = searchMap.get("sort");
        String sortOrder = searchMap.get("sortOrder");
        if (!"".equals(sort)) {
           searchSourceBuilder.sort(sort, SortOrder.valueOf(sortOrder));
       }

        searchRequest.source(searchSourceBuilder);




        // 2.封装查询结果
        Map resultMap = new HashMap();
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.out.println("记录缓存数:" + totalHits);
            SearchHit[] hits = searchHits.getHits();

            // 2.1商品列表
            ArrayList<Map<String, Object>> resultList = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map<String, Object> skuMap = hit.getSourceAsMap();

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightFieldName = highlightFields.get("name");
                Text[] fragments = highlightFieldName.fragments();

                skuMap.put("name", fragments[0].toString());
                resultList.add(skuMap);
            }
            resultMap.put("rows", resultList);


            // 2.2商品分类列表
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
            Terms terms = (Terms) aggregationMap.get("sku_category");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            ArrayList<String> categoryList = new ArrayList<>();

            for (Terms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();
                categoryList.add(keyAsString);
                resultMap.put("categoryList", categoryList);
            }

            // 2.3品牌列表

            //商品分类名称
            String categoryName = "";
            // 如果没有分类条件
            if (searchMap.get("category") == null) {
                if (categoryList.size() > 0) {
                    // 提取分类列表的第一个分类
                    categoryName = categoryList.get(0);
                }
            } else {
                categoryName = searchMap.get("category");
            }
            List<Map> brandList = brandMapper.findListByCategoryName(categoryName);
            resultMap.put("brandList", brandList);

            // 2.4 规格列表
            List<Map> specList = specMapper.findListByCategoryName(categoryName);
            for (Map spec : specList) {
                // 规格选项列表
                String[] options = ((String) spec.get("options")).split(",");
                spec.put("options", options);
            }
            resultMap.put("specList", specList);

            // 2.5页码
            long totalCount = searchHits.getTotalHits();// 总数
            long pageCount = (totalCount % pageSize == 0) ? totalCount / pageSize : (totalCount / pageSize + 1);
            resultMap.put("totalPages", pageCount);


        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultMap;

    }
}

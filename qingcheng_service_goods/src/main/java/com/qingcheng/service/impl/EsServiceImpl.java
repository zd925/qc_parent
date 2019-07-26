package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.EsService;
import com.qingcheng.service.goods.SkuService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-09    21:52
 */
@Service
public class EsServiceImpl implements EsService {
   @Autowired
   private SkuService skuService;
    /**
     * 导入索引库
     */

    @Override
    public void importToEs() throws IOException {
        // 1.连接rest接口
        HttpHost http = new HttpHost("127.0.0.1", 9200, "http");
        RestClientBuilder builder = RestClient.builder(http).setMaxRetryTimeoutMillis(60000000);// rest构建器
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);// 高级客户端连接

        // 判断是否已经有索引数据
        if (countEs(restHighLevelClient)>0){
            System.out.println("已经存在索引数据");
            return;
        }
        System.out.println("开始准备索引数据库");
        Map map=new HashMap();
        map.put("status",1);
        int pageNo=1;
        while(true){
            System.out.println("page:"+pageNo);
            PageResult page = skuService.findPage(map, pageNo, 1000);
            List<Sku> skuList = page.getRows();
            if (skuList.size()>0){
                importSkuListToEs(restHighLevelClient,skuList);
            }else{
                break;
            }
            pageNo++;
        }
        System.out.println("......索引库数据完成");
    }

    /**
     * 计算当前数据条数
     *
     * @param restHighLevelClient
     * @return
     */
    private long countEs(RestHighLevelClient restHighLevelClient) {
        // 2.封装查询请求
        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        // 3.获取查询结果
        SearchResponse searchResponse=null;
        try {
             searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("记录数:"+totalHits);
        return totalHits;
    }

    /**
     * 将sku列表导入索引库
     * @param restHighLevelClient
     * @param skuList
     */
    private void importSkuListToEs(RestHighLevelClient restHighLevelClient, List<Sku> skuList) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Sku sku : skuList) {
            IndexRequest indexRequest = new IndexRequest("sku", "doc", sku.getId().toString());
            Map skuMap= new HashMap();
            skuMap.put("name",sku.getName());
            skuMap.put("brandName",sku.getBrandName());
            skuMap.put("categoryName",sku.getCategoryName());
            skuMap.put("image",sku.getImage());
            skuMap.put("price",sku.getPrice());
            skuMap.put("createTime",sku.getCreateTime());
            skuMap.put("saleNum",sku.getSaleNum());
            skuMap.put("commentNum",sku.getCommentNum());
            skuMap.put("spec", JSON.parseObject(sku.getSpec(),Map.class));
            indexRequest.source(skuMap);
            bulkRequest.add(indexRequest);
        }
        System.out.println("开始导入索引库");
        //异步调用方式
        restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT,  new ActionListener<BulkResponse>() {
            public void onResponse(BulkResponse bulkResponse) {
                System.out.println("导入成功"+bulkResponse.status());//成功
            }
            public void onFailure(Exception e) {
                e.printStackTrace();
                System.out.println("导入失败"+e.getMessage());//失败
            }
        });
        System.out.println("导入完成");
    }
    }

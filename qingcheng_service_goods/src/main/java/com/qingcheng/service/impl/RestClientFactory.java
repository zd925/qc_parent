package com.qingcheng.service.impl;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-10    13:07
 */
public class RestClientFactory {
    public static RestHighLevelClient getRestHighLevelClient(String hostname,int port){
        HttpHost http = new HttpHost(hostname, port,"http");
        RestClientBuilder builder = RestClient.builder(http);// rest构建器
        return new RestHighLevelClient(builder);// 高级客户端对象(连接)
    }
}

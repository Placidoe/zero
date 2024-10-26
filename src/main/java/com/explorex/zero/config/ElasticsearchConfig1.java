//package com.explorex.zero.config;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ElasticsearchConfig1 {
//
//
//    @Bean(destroyMethod = "close")
//    public RestHighLevelClient restHighLevelClient() {
//        String hostname = System.getProperty("spring.elasticsearch.uris", "192.168.253.144:9200");
//        int port = 9200; // 默认端口
//        String protocol = "http";
//
//        if (hostname.contains(":")) {
//            String[] parts = hostname.split(":");
//            hostname = parts[0];
//            port = Integer.parseInt(parts[1]);
//            System.out.println(parts[0]);
//            System.out.println(parts[1]);
//        }
//
//        return new RestHighLevelClient(
//                RestClient.builder(new HttpHost(hostname, port, protocol))
//        );
//    }
//}
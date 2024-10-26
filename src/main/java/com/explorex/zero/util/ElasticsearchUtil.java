package com.explorex.zero.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticsearchUtil {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public ElasticsearchUtil(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    /**
     * 批量查询文档
     *
     * @param indexName 索引名称
     * @param pageable  分页信息
     * @param clazz     文档类型
     * @param <T>       文档类型
     * @return 查询结果
     */
    public <T> List<T> searchDocumentsInBatch(String indexName, Pageable pageable, Class<T> clazz) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withPageable(pageable);

        SearchHits<T> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, IndexCoordinates.of(indexName));
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 单个字段的模糊查询
     *
     * @param indexName 索引名称
     * @param field     字段名
     * @param value     查询值
     * @param pageable  分页信息
     * @param clazz     文档类型
     * @param <T>       文档类型
     * @return 查询结果
     */
    public <T> List<T> fuzzySearchSingleField(String indexName, String field, String value, Pageable pageable, Class<T> clazz) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.fuzzyQuery(field, value))
                .withPageable(pageable);

        SearchHits<T> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, IndexCoordinates.of(indexName));
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 根据多个字符串进行模糊查询
     *
     * @param indexName 索引名称
     * @param field     字段名
     * @param values    查询值列表
     * @param pageable  分页信息
     * @param clazz     文档类型
     * @param <T>       文档类型
     * @return 查询结果
     */
    public <T> List<T> fuzzySearchMultipleValues(String indexName, String field, List<String> values, Pageable pageable, Class<T> clazz) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for (String value : values) {
            boolQuery.should(QueryBuilders.fuzzyQuery(field, value));
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable);

        SearchHits<T> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, IndexCoordinates.of(indexName));
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
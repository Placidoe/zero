package com.explorex.zero.util;

import com.explorex.zero.task.FuzzyQueryTask;
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
import java.util.concurrent.ForkJoinPool;
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
     * 使用 Fork/Join 优化，确保每个子任务处理的 values 大小不超过 100
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
        // 使用 Fork/Join 框架执行查询

        // 使用 Runtime 类
        int availableProcessors = Runtime.getRuntime().availableProcessors()-1;
        int threshold = values.size() / availableProcessors;

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FuzzyQueryTask<T> task = new FuzzyQueryTask<>(elasticsearchRestTemplate, indexName, field, values, clazz, threshold);
        List<SearchHit<T>> searchHits = forkJoinPool.invoke(task);

        // 将搜索结果转换为文档列表
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
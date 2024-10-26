package com.explorex.zero.task;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FuzzyQueryTask<T> extends RecursiveTask<List<SearchHit<T>>> {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final String indexName;
    private final String field;
    private final List<String> values;
    private final Class<T> clazz;
    private final int threshold;

    public FuzzyQueryTask(ElasticsearchRestTemplate elasticsearchRestTemplate, String indexName, String field, List<String> values, Class<T> clazz, int threshold) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.indexName = indexName;
        this.field = field;
        this.values = values;
        this.clazz = clazz;
        this.threshold = threshold;
    }

    @Override
    protected List<SearchHit<T>> compute() {
        if (values.size() <= threshold) {
            // 如果值的数量小于等于阈值，则直接执行查询
            return executeFuzzyQueries(values);
        } else {
            // 否则，将任务分解为四个子任务
            int quarter1 = values.size() / 4;
            int quarter2 = 2 * quarter1;
            int quarter3 = 3 * quarter1;

            FuzzyQueryTask<T> task1 = new FuzzyQueryTask<>(elasticsearchRestTemplate, indexName, field, values.subList(0, quarter1), clazz, threshold);
            FuzzyQueryTask<T> task2 = new FuzzyQueryTask<>(elasticsearchRestTemplate, indexName, field, values.subList(quarter1, quarter2), clazz, threshold);
            FuzzyQueryTask<T> task3 = new FuzzyQueryTask<>(elasticsearchRestTemplate, indexName, field, values.subList(quarter2, quarter3), clazz, threshold);
            FuzzyQueryTask<T> task4 = new FuzzyQueryTask<>(elasticsearchRestTemplate, indexName, field, values.subList(quarter3, values.size()), clazz, threshold);

            // 异步执行所有子任务
            task1.fork();
            task2.fork();
            task3.fork();

            // 同步执行最后一个子任务
            List<SearchHit<T>> result4 = task4.compute();

            // 等待其他子任务完成并获取结果
            List<SearchHit<T>> result1 = task1.join();
            List<SearchHit<T>> result2 = task2.join();
            List<SearchHit<T>> result3 = task3.join();

            // 合并所有子任务的结果
            List<SearchHit<T>> result = new ArrayList<>();
            result.addAll(result1);
            result.addAll(result2);
            result.addAll(result3);
            result.addAll(result4);
            return result;
        }
    }

    private List<SearchHit<T>> executeFuzzyQueries(List<String> values) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (String value : values) {
            boolQuery.should(QueryBuilders.fuzzyQuery(field, value));
        }

        SearchHits<T> searchHits = elasticsearchRestTemplate.search((Query) boolQuery, clazz, IndexCoordinates.of(indexName));
        return searchHits.getSearchHits();
    }
}
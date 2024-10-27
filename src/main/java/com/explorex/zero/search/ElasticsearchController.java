package com.explorex.zero.search;

import com.explorex.zero.common.entity.MyDocument;
import com.explorex.zero.util.ElasticsearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElasticsearchController {

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    /**
     * 批量查询接口
     * @param page 页码
     * @param size 每页大小
     * @return 查询结果
     */
    @GetMapping("/batch-search")
    public List<MyDocument> batchSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return elasticsearchUtil.searchDocumentsInBatch("my_index", pageable, MyDocument.class);
    }

    /**
     * 单个模糊查询接口
     * @param field 字段名
     * @param value 查询值
     * @param page 页码
     * @param size 每页大小
     * @return 查询结果
     */
    @GetMapping("/fuzzy-search")
    public List<MyDocument> fuzzySearch(
            @RequestParam String field,
            @RequestParam String value,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return elasticsearchUtil.fuzzySearchSingleField("my_index", field, value, pageable, MyDocument.class);
    }

    /**
     * 多字符串模糊查询接口
     * @param field 字段名
     * @param values 查询值列表
     * @param page 页码
     * @param size 每页大小
     * @return 查询结果
     */
    @GetMapping("/fuzzy-search-multiple-values")
    public List<MyDocument> fuzzySearchMultipleValues(
            @RequestParam String field,
            @RequestParam List<String> values,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return elasticsearchUtil.fuzzySearchMultipleValues("my_index", field, values, pageable, MyDocument.class);
    }
}
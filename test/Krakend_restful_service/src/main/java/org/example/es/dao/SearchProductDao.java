package org.example.es.dao;

import org.example.es.entity.SearchProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * es dao
 * @author liqi
 *
 */
public interface SearchProductDao extends ElasticsearchRepository<SearchProduct, Integer> {

}
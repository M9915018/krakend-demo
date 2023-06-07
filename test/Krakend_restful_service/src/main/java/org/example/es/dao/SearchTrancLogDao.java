package org.example.es.dao;


import org.example.es.entity.SearchTrancLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * es dao
 * @author liqi
 *
 */
public interface SearchTrancLogDao extends ElasticsearchRepository<SearchTrancLog, Integer> {

}
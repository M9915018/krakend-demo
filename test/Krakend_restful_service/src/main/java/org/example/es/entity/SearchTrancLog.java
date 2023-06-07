package org.example.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * es存库的实体
 * @author liqi
 * @data 2022年5月16日 下午4:20:33
 */
@Document(indexName = "ic-*", shards = 3)
@Data
public class SearchTrancLog implements Serializable {

    private static final long serialVersionUID = -6629478619791342054L;


    /**主键*/
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String message;

    @Field(type = FieldType.Keyword)
    private String log_type;

    private String[] traceID;

    @Field (name = "response.Timestamp")
    private long timestamp;

    private String spanID;

    private String parent_spanID;

    private String type;

    private String source_host;

    @Field (name = "response.Path")
    private long path;

    @Field (name = "response.Method")
    private long method;

    @Field (name = "response.StatusCode")
    private long statusCode;



}


package org.example.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * es存库的实体
 * @author liqi
 * @data 2022年5月16日 下午4:20:33
 */
@Document(indexName = "test_product", shards = 3)
@Data
public class SearchProduct implements Serializable {

    private static final long serialVersionUID = -6629478619791342054L;

    /*
    *     "id" : 5,
          "skuId" : "sku万宝龙",
          "name" : "万宝龙",
          "price" : 4.1
    *
    * */

    /**主键*/
    @Id
    private Integer id;

    /**skuId*/
    @Field(type = FieldType.Keyword)
    private String skuId;

    /**名称1*/
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    /**价格*/
    private Double price;

}


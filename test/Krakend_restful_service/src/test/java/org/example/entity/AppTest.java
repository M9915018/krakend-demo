package org.example.entity;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.App;
import org.example.es.entity.SearchProduct;
import org.example.es.dao.SearchProductDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class })
public class AppTest {

    @Autowired
    SearchProductDao searchProductDao;

    @Test
    public void save() {
        List<SearchProduct> list = new ArrayList<SearchProduct>();
        String names = "中天\n" + "敏杨\n" + "名爵尔\n" + "万宝龙\n" + "万宝龙\n" + "巴黎水";
        String[] split = names.split("\n");
        for (int i = 0; i < split.length; i++) {
            SearchProduct searchProduct = new SearchProduct();
            searchProduct.setId(i + 1);
            searchProduct.setName(split[i]);
            searchProduct.setSkuId("sku" + split[i]);
            searchProduct.setPrice(i + 0.1D);
            list.add(searchProduct);
        }
        searchProductDao.saveAll(list);
    }
    @Test
    public void search() {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("name", "中天"));
        Iterable<SearchProduct> search = searchProductDao.search(builder);
        for (SearchProduct searchProduct : search) {
            System.err.println(searchProduct);
        }
    }

}
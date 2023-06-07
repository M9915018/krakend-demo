package org.example.controller;



import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.es.dao.SearchProductDao;
import org.example.es.dao.SearchTrancLogDao;
import org.example.es.entity.SearchProduct;
import org.example.es.entity.SearchTrancLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JavaDeveloperZone.
 */
@RestController
@RequestMapping("/esapi")
public class ESController {
    @Autowired
    private SearchTrancLogDao searchTrancLogDao;

    @RequestMapping()
    public String SpringBootESExample() {
        return "Welcome to Spring Boot Elastic Search Example";
    }
    @DeleteMapping("/delete")
    public String deleteAllProducts() {
        try {   //delete all documents from solr core
            searchTrancLogDao.deleteAll();
            return "documents deleted succesfully!";
        }catch (Exception e){
            return "Failed to delete documents";
        }
    }
    @PostMapping("/save")
    public String saveAllProducts() {
        //Store Documents
//        documentRepository.saveAll(Arrays.asList(new SearchProduct("1", "pdf","Java Dev Zone"),
//                new SearchProduct("2", "msg", "subject:reinvetion"),
//                new SearchProduct("3", "pdf", "Spring boot sessions"),
//                new SearchProduct("4", "docx", "meeting agenda"),
//                new SearchProduct("5", "docx", "Spring boot + Elastic Search")));

//        List<SearchProduct> list = new ArrayList<SearchProduct>();
//        String names = "中天\n" + "敏杨\n" + "名爵尔\n" + "万宝龙\n" + "万宝龙\n" + "巴黎水";
//        String[] split = names.split("\n");
//        for (int i = 0; i < split.length; i++) {
//            SearchProduct searchProduct = new SearchProduct();
//            searchProduct.setId(i + 1);
//            searchProduct.setName(split[i]);
//            searchProduct.setSkuId("sku" + split[i]);
//            searchProduct.setPrice(i + 0.1D);
//            list.add(searchProduct);
//        }
//        searchProductDao.saveAll(list);


        return "5 documents saved!!!";
    }



    @GetMapping("/pushTempo/{tranc}")
    public Map<String,SearchTrancLog> pushTrancLogsToTempo( @PathVariable String tranc) {
        Map<String,SearchTrancLog> documents = new HashMap();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("traceID", tranc));
        Iterable<SearchTrancLog> search = searchTrancLogDao.search(builder);
        for (SearchTrancLog searchTrancLog : search) {
            System.err.println(searchTrancLog);
            documents.put("",searchTrancLog);
        }
        return documents;
    }

    @GetMapping("/get/{tranc}")
    public List<SearchTrancLog> getTrancLogs( @PathVariable String tranc) {
        List<SearchTrancLog> documents = new ArrayList<>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("traceID", tranc));
        Iterable<SearchTrancLog> search = searchTrancLogDao.search(builder);
        for (SearchTrancLog searchTrancLog : search) {
            System.err.println(searchTrancLog);
            documents.add(searchTrancLog);
        }
        return documents;
    }

    @GetMapping("/getAll")
    public List<SearchTrancLog> getAllProducts() {
        List<SearchTrancLog> documents = new ArrayList<>();
        // iterate all documents and add it to list
        for (SearchTrancLog doc : this.searchTrancLogDao.findAll()) {
            documents.add(doc);
        }

//        BoolQueryBuilder builder = QueryBuilders.boolQuery();
//        // 搜索关键词
//        builder.must(QueryBuilders.matchQuery("traceID", "21856afEd15B04ae1686035132716045"));
//        Iterable<SearchTrancLog> search = searchTrancLogDao.search(builder);
//        for (SearchTrancLog searchTrancLog : search) {
//            System.err.println(searchTrancLog);
//            documents.add(searchTrancLog);
//        }

        return documents;
    }
}
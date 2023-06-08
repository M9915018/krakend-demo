package org.example.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.es.bean.TempoAPIBean;
import org.example.es.dao.SearchProductDao;
import org.example.es.dao.SearchTrancLogDao;
import org.example.es.entity.SearchProduct;
import org.example.es.entity.SearchTrancLog;
import org.example.httpclint.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public String pushTrancLogsToTempo( @PathVariable String tranc) {
        String str_tmp ="";
        ArrayList<TempoAPIBean> list  = new ArrayList<>();
        Map<String,SearchTrancLog> map = new HashMap<>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("traceID", tranc));
        Iterable<SearchTrancLog> search = searchTrancLogDao.search(builder);
        for (SearchTrancLog searchProduct : search) {
            if(!map.containsKey(searchProduct.getSpanID())) // 不存在就先放著，要一組才好處理
                map.put(searchProduct.getSpanID(),searchProduct);
            else { // 一組的就能處理計算
                long cost=0;
                SearchTrancLog  tmp= map.get(searchProduct.getSpanID());
                if (tmp.getType().equals("Resp")){
                    cost=tmp.getTimestamp()-searchProduct.getTimestamp();
                } else {
                    cost=searchProduct.getTimestamp()-tmp.getTimestamp();
                }

                //TempoAPIBean tbean = new TempoAPIBean(tmp.getSpanID(),tmp.getTraceID()[0],tmp.getTimestamp(),cost,tmp.getId(),tmp.getParent_spanID());
                TempoAPIBean tbean = new TempoAPIBean(tmp,cost);

                list.add(tbean);
                map.remove(searchProduct.getSpanID()); // 處理完從map拿掉
            }
            System.out.println(searchProduct);
        }
        // 最後要再把沒有一組的做後續處理
        // 使用foreach循環遍歷HashMap的物件
        for (Map.Entry<String, SearchTrancLog> entry : map.entrySet()) {
            String key = entry.getKey();
            SearchTrancLog value = entry.getValue();
//            System.out.println("Key: " + key + ", Value: " + value);
            TempoAPIBean tbean = new TempoAPIBean(value,0);
            list.add(tbean);
        }


        System.err.println(map);
        System.err.println("tempo:"+list);

        // list 數據要轉換成json格式
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(list);
            System.out.println(json);

            // 這邊就要呼叫okhttpclient 把json請求推到tempo api
            OkHttpClient client1=new OkHttpClient();
            // 查詢api的方法參數丟list和每次查得批量
            str_tmp =client1.bathQueryApi(json);
            System.out.println(str_tmp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str_tmp;
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
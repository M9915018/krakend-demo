package org.example.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.App;
import org.example.es.bean.TempoAPIBean;
import org.example.es.dao.SearchProductDao;
import org.example.es.dao.SearchTrancLogDao;
import org.example.es.entity.SearchProduct;
import org.example.es.entity.SearchTrancLog;
import org.example.httpclint.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class })
public class TrancLogTest {

    @Autowired
    SearchTrancLogDao searchTrancLogDao;

    @Test
    public void save() {
        // index 名稱用萬用字元會沒有辦法寫數據
        List<SearchTrancLog> list = new ArrayList<SearchTrancLog>();
        String names = "中天\n" + "敏杨\n" + "名爵尔\n" + "万宝龙\n" + "万宝龙\n" + "巴黎水";
        String[] tid = {"21856afEd15B04ae1686035132716045"};

        String[] split = names.split("\n");
        for (int i = 0; i < split.length; i++) {
            SearchTrancLog searchProduct = new SearchTrancLog();
            searchProduct.setId(String.valueOf(i + 1));
            searchProduct.setMessage(split[i]);
            searchProduct.setLog_type("K-TrancLog");
            searchProduct.setTraceID(tid);
            list.add(searchProduct);
        }
        searchTrancLogDao.saveAll(list);
    }
    @Test
    public void search() {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("traceID", "21856afEd15B04ae1686035132716045"));
        Iterable<SearchTrancLog> search = searchTrancLogDao.search(builder);
        for (SearchTrancLog searchProduct : search) {
            System.err.println(searchProduct);
        }
    }

    @Test
    public void searchAndGetCost() {
        ArrayList<TempoAPIBean> list  = new ArrayList<>();
        Map<String,SearchTrancLog> map = new HashMap<>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 搜索关键词
        builder.must(QueryBuilders.matchQuery("traceID", "2595728C5aC677b91686063562315582"));
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
            String str_tmp =client1.bathQueryApi(json);
            System.out.println(str_tmp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void testRandom() {
        Random random = new Random();
        // 生成1到5之間的隨機數
        int randomNumber = random.nextInt(5);
        // 輸出結果
        System.out.println("隨機數: " + randomNumber);
    }

    @Test
    public void  HashMapExample() {
        // 創建一個HashMap
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("A", 1);
        hashMap.put("B", 2);
        hashMap.put("C", 3);

        // 使用foreach循環遍歷HashMap的物件
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
            // 在這裡進行物件處理
        }
    }

    @Test
    public void ArrayListToJsonExample(){
        // 創建一個ArrayList並添加元素
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Orange");

        // 使用Jackson庫將ArrayList轉換為JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(arrayList);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAppBathQueryApi() throws IOException {
        String json="[{\"id\":\"2595728C5aC677b9\",\"traceId\":\"2595728C5aC677b91686063562315582\",\"parentId\":null,\"timestamp\":1686063562315582273,\"duration\":15065708,\"name\":\"dUk4kYgBR_x-qPJEPofx\",\"tags\":{},\"localEndpoint\":{\"serviceName\":\"tmax\"}},{\"id\":\"25957ABfFb168Ef0\",\"traceId\":\"2595728C5aC677b91686063562315582\",\"parentId\":null,\"timestamp\":1686063562327647116,\"duration\":4600354,\"name\":\"d0k4kYgBR_x-qPJEPof6\",\"tags\":{},\"localEndpoint\":{\"serviceName\":\"numberfron\"}}]\n/get\",\"Method\":\"POST\"},\"localEndpoint\":{\"serviceName\":\"tmax\"}},{\"id\":\"25957ABfFb168Ef0\",\"traceId\":\"2595728C5aC677b91686063562315582\",\"parentId\":\"2595728C5aC677b9\",\"timestamp\":1686063562327647116,\"duration\":4600354,\"name\":\"d0k4kYgBR_x-qPJEPof6\",\"tags\":{},\"localEndpoint\":{\"serviceName\":\"SCM\"}}]\n/get\",\"Method\":\"POST\"},\"localEndpoint\":{\"serviceName\":\"tmax\"}},{\"id\":\"25957ABfFb168Ef0\",\"traceId\":\"2595728C5aC677b91686063562315582\",\"parentId\":\"2595728C5aC677b9\",\"timestamp\":1686063562327647116,\"duration\":4600354,\"name\":\"d0k4kYgBR_x-qPJEPof6\",\"tags\":{},\"localEndpoint\":{\"serviceName\":\"account_service\"}}]\n/get\",\"Method\":\"POST\"},\"localEndpoint\":{\"serviceName\":\"SCM\"}},{\"id\":\"25957ABfFb168Ef0\",\"traceId\":\"2595728C5aC677b91686063562315582\",\"parentId\":\"2595728C5aC677b9\",\"timestamp\":1686063562327647116,\"duration\":4600354,\"name\":\"d0k4kYgBR_x-qPJEPof6\",\"tags\":{\"Path\":null,\"Method\":null},\"localEndpoint\":{\"serviceName\":\"moec\"}}]\n";

        OkHttpClient client1=new OkHttpClient();
        // 查詢api的方法參數丟list和每次查得批量
        String map =client1.bathQueryApi(json);
//        assertTrue(map.get("9789861755267").equals("1"));

    }

}
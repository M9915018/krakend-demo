package org.example.httpclint;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.util.List;

public class OkHttpClient {

    okhttp3.OkHttpClient client;
    MediaType mediaType;
    String url;

    StringBuffer sb;

    ObjectMapper objectMapper = new ObjectMapper();

    public OkHttpClient(){
        client = new okhttp3.OkHttpClient();
        mediaType = MediaType.parse("application/json");
        this.url  ="http://192.168.56.1:9411";
        sb=new StringBuffer();
    }

    public String bathQueryApi(String jsonbody) throws IOException {
//        StringBuffer sb = getStringBuffer(list);

        //while (true){
        RequestBody body = RequestBody.create(mediaType, jsonbody);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("response.body().string():"+ response.body().toString());
//        TaxBookApiResponse tbapi = objectMapper.readValue(response.body().string(), TaxBookApiResponse.class);
//        ArrayList<SubData> subDataList = tbapi.getData();
//        HashMap map = new HashMap(list.size());
//        for(SubData sdata:subDataList) {
//            if(sdata.getStatus().equals("approved"))
//                map.put(sdata.getIsbn(), "1");
//        }

        // key 不存在反饋結果的以0當應扣稅處理
//        for(String key:list){
//            if(!map.containsKey(key)){
//                map.put(key, "0");
//            }
//        }

        return response.toString();

    }

    @NotNull
    private StringBuffer getStringBuffer(List<String> list) {
        sb.setLength(0);// 清空
        //"isbn=9789861755267&isbn=9789861755268"
        int count=0;
        for(String isbn: list){
            if(count>0){
                sb.append("&");
            }
            sb.append("isbn=");
            sb.append(isbn);
            count++;
        }
        return sb;
    }
}

package org.example.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HelloWorld {
    @RequestMapping()
    public String add() {
        return "Hello World";
    }


    @RequestMapping("/test")
    public ResponseEntity<String> test(@RequestHeader Map<String,String> map_h) {

        HttpHeaders responseHeaders = new HttpHeaders();
        if(map_h.containsKey("krakend_tx_id")){
        responseHeaders.set("Krakend_span_id", map_h.get("krakend_span_id"));
        responseHeaders.set("Krakend_parent_span_id", map_h.get("krakend_parent_span_id"));
        responseHeaders.set("Krakend_tx_id", map_h.get("krakend_tx_id"));}
        return ResponseEntity.ok().headers(responseHeaders).body(map_h.toString());
    }

    @PostMapping("/get")
    public ResponseEntity<String> get(@RequestHeader Map<String,String> map_h, @RequestBody String map) throws IOException {
        String result= map.toString();
        //System.err.println("result:"+result);
        System.out.println("headers:"+map_h);
        System.out.println("headers.krakend_span_id:"+map_h.get("krakend_span_id").toString());


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "isbn=9789861755267");
//        Request request = new Request.Builder()
//                .url("https://tax.moc.gov.tw/book-api/v2/announcement/isbn/multi?appid=9uKnzW")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .build();

        String json="{\"Reason\": \"" + map + "\"}";
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, JSON); // new
        // RequestBody body = RequestBody.create(JSON, json); // old
        String url="http://192.168.56.1:8081/ic";
        Request request = new Request.Builder()
                .url(url).get()
                .addHeader("Krakend_parent_span_id", map_h.get("krakend_span_id"))
                .addHeader("Krakend_tx_id", map_h.get("krakend_tx_id"))
                .build();
        System.out.println("request.headers():"+request.headers().toString());
        Response response = client.newCall(request).execute();
        //return response.body().string();

        HttpHeaders responseHeaders = new HttpHeaders();
        if(map_h.containsKey("krakend_tx_id")){
        responseHeaders.set("Krakend_span_id", map_h.get("krakend_span_id"));
        responseHeaders.set("Krakend_parent_span_id", map_h.get("krakend_parent_span_id"));
        responseHeaders.set("Krakend_tx_id", map_h.get("krakend_tx_id"));}

        return ResponseEntity.ok().headers(responseHeaders).body(response.toString());

        //return ResponseEntity.ok(response.toString());
    }

}
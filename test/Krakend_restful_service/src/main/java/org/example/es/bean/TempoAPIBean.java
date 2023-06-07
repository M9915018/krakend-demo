package org.example.es.bean;

import lombok.Data;
import org.example.es.entity.SearchTrancLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
public class TempoAPIBean {
    private String id;
    private String traceId;
    private String parentId;
    private long timestamp;
    private long duration;
    private String name;
    private Map<String, Object> tags;
    private LocalEndpoint localEndpoint;

    public TempoAPIBean(SearchTrancLog tmp, long cost) {
        this.id = tmp.getSpanID();
        this.traceId = tmp.getTraceID()[0];
        this.timestamp = tmp.getTimestamp();
        this.name = tmp.getId();
        this.duration = cost;

        if(tmp.getParent_spanID() != null && tmp.getParent_spanID() != "" )
            this.parentId = tmp.getParent_spanID();

        // 增加tags 區塊數據
        tags = new HashMap<>();
        tags.put("Path",tmp.getPath());
        tags.put("Method", tmp.getMethod());

        // serviceName先暫時製作假數據 之後再接真實數據
        String[] serviceName = {"account_service","tmax","moec","numberfron","SCM","middle"};
        // 創建一個隨機數生成器
        Random random = new Random();
        // 生成1到5之間的隨機數
        this.localEndpoint =new LocalEndpoint(serviceName[random.nextInt(5)]);

    }

    @Data
    class LocalEndpoint {
    private String serviceName;

    // 空的預設建構子
    public LocalEndpoint(String serviceName) {
        this.serviceName = serviceName;
    }

}
}

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
    private long duration=0;
    private String name;
    private Map<String, Object> tags;
    private LocalEndpoint localEndpoint;

    public TempoAPIBean(SearchTrancLog tmp, long cost) {
        this.id = tmp.getSpanID();
        this.traceId = tmp.getTraceID()[0];

        this.timestamp = tmp.getTimestamp()/1000;
        this.name = tmp.getId();
        if(cost > 0)
        this.duration = cost/1000;

        if(tmp.getParent_spanID() != null && !tmp.getParent_spanID().equals("") && !tmp.getParent_spanID().equals("-1"))
            this.parentId = tmp.getParent_spanID();

        // 增加tags 區塊數據
        tags = new HashMap<>();
        if(tmp.getPath() != null && !tmp.getPath().equals("") )
        tags.put("Path",tmp.getPath());
        if(tmp.getMethod() != null && !tmp.getMethod().equals("") )
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

package com.example.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.domain.LogDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ElasticsearchClient elasticsearchClient;

    public Map<String, Object> getLogStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            var response = elasticsearchClient.search(s -> s
                    .index("logstash-*")
                    .size(0)
                    .aggregations("levelCount", a -> a
                            .terms(t -> t
                                    .field("level.keyword")
                            )
                    )
                    .aggregations("timeHistogram", a -> a
                            .dateHistogram(h -> h
                                    .field("timestamp")
                                    .calendarInterval(CalendarInterval.Hour)
                            )
                    ), LogDocument.class);

            stats.put("levelStats", response.aggregations().get("levelCount").sterms().buckets().array());
            stats.put("timeStats", response.aggregations().get("timeHistogram").dateHistogram().buckets().array());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
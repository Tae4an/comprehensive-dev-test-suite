package com.example.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import com.example.domain.LogDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
@RequiredArgsConstructor
public class LogRepository {
    private final ElasticsearchClient elasticsearchClient;

    public Page<LogDocument> searchLogs(String startTime, String endTime, List<String> levels, String keyword, Pageable pageable) {
        try {
            var query = new BoolQuery.Builder();

            if (startTime != null && endTime != null) {
                query.must(m -> m
                        .range(r -> r
                                .field("timestamp")
                                .gte(JsonData.of(startTime))
                                .lte(JsonData.of(endTime))
                        )
                );
            }

            if (levels != null && !levels.isEmpty()) {
                query.must(m -> m
                        .match(mt -> mt
                                .field("level.keyword")
                                .query(String.join(" ", levels))
                        )
                );
            }

            if (keyword != null && !keyword.isEmpty()) {
                query.must(m -> m
                        .match(mt -> mt
                                .field("message")
                                .query(keyword)
                        )
                );
            }

            var response = elasticsearchClient.search(s -> s
                            .index("logstash-*")
                            .from(pageable.getPageNumber() * pageable.getPageSize())
                            .size(pageable.getPageSize())
                            .query(query.build()._toQuery()),
                    LogDocument.class
            );

            return new PageImpl<>(
                    response.hits().hits().stream()
                            .map(hit -> hit.source())
                            .toList(),
                    pageable,
                    response.hits().total().value()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to search logs", e);
        }
    }

    public Map<String, Long> getLogLevelCounts() {
        try {
            var response = elasticsearchClient.search(s -> s
                            .index("logstash-*")
                            .size(0)
                            .aggregations("levelCount", a -> a
                                    .terms(t -> t
                                            .field("level.keyword")
                                    )
                            ),
                    LogDocument.class
            );

            var buckets = response.aggregations()
                    .get("levelCount")
                    .lterms()
                    .buckets().array();

            Map<String, Long> results = new HashMap<>();
            for (var bucket : buckets) {
                results.put(String.valueOf(bucket.key()), bucket.docCount());
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get log level counts", e);
        }
    }
}
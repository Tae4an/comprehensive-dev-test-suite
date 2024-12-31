package com.example.service;


import com.example.domain.LogDocument;
import com.example.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public Page<LogDocument> searchLogs(String startTime, String endTime, List<String> levels, String keyword, Pageable pageable) {
        return logRepository.searchLogs(startTime, endTime, levels, keyword, pageable);
    }

    public Map<String, Long> getLogLevelCounts() {
        return logRepository.getLogLevelCounts();
    }
}
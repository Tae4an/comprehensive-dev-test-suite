package com.example.controller;

import com.example.domain.LogDocument;
import com.example.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/logs")
    public String searchLogs(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) List<String> levels,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            Model model
    ) {
        Page<LogDocument> logs = logService.searchLogs(startTime, endTime, levels, keyword, pageable);
        model.addAttribute("logs", logs);
        return "list";
    }

    @GetMapping("/")
    public String searchForm() {
        return "search";
    }
}
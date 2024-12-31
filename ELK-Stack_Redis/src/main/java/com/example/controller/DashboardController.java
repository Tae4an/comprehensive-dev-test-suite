package com.example.controller;

import com.example.service.DashboardService;
import com.example.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final LogService logService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("logCounts", logService.getLogLevelCounts());
        return "dashboard";
    }
}
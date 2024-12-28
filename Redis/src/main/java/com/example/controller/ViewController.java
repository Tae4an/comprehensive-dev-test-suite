package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * 메인 페이지로 이동
     * @return index.jsp
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 문자열 관리 페이지로 이동
     * @return sections/string.jsp
     */
    @GetMapping("/string")
    public String stringManagement() {
        return "sections/string";
    }

    /**
     * 리스트 관리 페이지로 이동
     * @return sections/list.jsp
     */
    @GetMapping("/list")
    public String listManagement() {
        return "sections/list";
    }

    /**
     * 이미지 캐시 관리 페이지로 이동
     * @return sections/image.jsp
     */
    @GetMapping("/image")
    public String imageManagement() {
        return "sections/image";
    }
}
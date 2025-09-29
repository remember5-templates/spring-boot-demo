package com.remember5.junit.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author wangjiahao
 * @date 2021/12/3
 */
@Slf4j
@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping
    public ModelAndView get(ModelAndView mv){
        log.info("====>>跳转freemarker页面");
        mv.addObject("name", "jack");
        mv.setViewName("pdf_export_employee_kpi");
        return mv;
    }

    @PostMapping("a")
    public String post(HttpServletRequest request, @RequestBody String jsonObject){
        log.info("{}");

        request.getParameterMap().forEach((key, value) -> {
            log.info("{} = {}", key, value);
        });

        return "";
    }


}

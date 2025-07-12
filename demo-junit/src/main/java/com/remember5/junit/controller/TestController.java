package com.remember5.junit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}

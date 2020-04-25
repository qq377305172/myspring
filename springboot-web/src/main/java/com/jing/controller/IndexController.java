package com.jing.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Admin
 * @time 2020/4/25 12:12
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/{id}")
    public String list(@PathVariable("id") Long id) {
        return String.valueOf(id);
    }
}

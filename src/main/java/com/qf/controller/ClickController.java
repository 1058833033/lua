package com.qf.controller;

import com.qf.pojo.ClickNumber;
import com.qf.service.IClickService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ChenJie
 * @date 2020-06-08 20:17:12
 * 功能说明
 */
@RestController
public class ClickController {
    @Resource
    private IClickService clickService;
    @RequestMapping("/click")
    @ResponseBody
    public String click(){
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                clickService.click();
            }).start();
        }

        return "ok";
    }
}

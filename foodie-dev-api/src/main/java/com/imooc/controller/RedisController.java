package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@ApiIgnore //绝对不会在swagger日志中显示
@RestController
@RequestMapping("redis")

public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value){
//        redisTemplate.opsForValue().set(key,value);
        redisOperator.set(key,value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(String key){
//        return (String) redisTemplate.opsForValue().get(key);
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key){

//        redisTemplate.delete(key);
        redisOperator.del(key);
        return "OK";
    }

    @GetMapping("/getAlot")
    public Object getAlot(String... keys){

        List<String> result = new ArrayList<>();
        for (String k:keys){
            result.add(redisOperator.get(k));

        }
        return result;
    }
}

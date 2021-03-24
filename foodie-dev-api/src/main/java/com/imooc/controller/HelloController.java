package com.imooc.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore //绝对不会在swagger日志中显示
@Api(value="注册登录", tags={"用于注册登录的相关接口"})//修改swagger日志中的标注
@RestController
public class HelloController {
    @GetMapping("/hello")
    public Object hello(){
        return "HELLO World";
    }
}

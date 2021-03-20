package controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HelloController {
    @GetMapping("/hello")
    public Object hello(){
        return "HELLO World";
    }
}

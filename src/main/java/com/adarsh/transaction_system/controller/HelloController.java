package com.adarsh.transaction_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Banking System 🚀";
    }
    @GetMapping("/continue")
    public String con(){
        return "It is working";
    }
//    @GetMapping("/")
//    public String home() {
//        return "Home page working!";
//    }
    @GetMapping("/about")
    public String about() {
        return "This is my first Spring Boot project";
    }
    @GetMapping("/hello-user")
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }
    @PostMapping("/hello-user")
    public String helloPost(@RequestParam String name,@RequestParam int age) {
        return "Hello " + name + ", age " + age;
    }
}

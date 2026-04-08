package com.adarsh.transaction_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.adarsh.transaction_system.entity.User;
import com.adarsh.transaction_system.repository.UserRepository;

@Controller
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Banking System 🚀";
    }

    @GetMapping("/hello-user")
    @ResponseBody
    public String hello(@RequestParam(required = false) String name) {
        if (name == null) {
            return "Please provide a name";
        }
        return "Hello " + name;
    }

    @GetMapping("/about")
    @ResponseBody
    public String about() {
        return "This is my first Spring Boot project";
    }

    @GetMapping("/success")
    @ResponseBody
    public String success() {
        return "User saved successfully 🎉";
    }

    @PostMapping("/hello-user")
    public String saveUser(@RequestParam String name,
                           @RequestParam int age) {

        User user = new User();
        user.setName(name);
        user.setAge(age);

        userRepository.save(user);

        return "redirect:/success";
    }
    @GetMapping("/users")
    @ResponseBody
    public String getUsers() {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>All Users</h2>");
        html.append("<table border='1' style='border-collapse: collapse;'>");
        html.append("<tr><th>ID</th><th>Name</th><th>Age</th></tr>");

        userRepository.findAll().forEach(user -> {
            html.append("<tr>")
                    .append("<td>").append(user.getId()).append("</td>")
                    .append("<td>").append(user.getName()).append("</td>")
                    .append("<td>").append(user.getAge()).append("</td>")
                    .append("</tr>");
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }
}
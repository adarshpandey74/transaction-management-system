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

    @GetMapping("/edit-user")
    @ResponseBody
    public String editUser(@RequestParam Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return "User not found";
        }

        return "<html><body>" +
                "<h2>Edit User</h2>" +
                "<form action='/update-user' method='post'>" +
                "<input type='hidden' name='id' value='" + user.getId() + "' />" +
                "<input type='text' name='name' value='" + user.getName() + "' /><br/>" +
                "<input type='number' name='age' value='" + user.getAge() + "' /><br/>" +
                "<button type='submit'>Update</button>" +
                "</form>" +
                "</body></html>";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam int age) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.setName(name);
            user.setAge(age);
            userRepository.save(user);
        }

        return "redirect:/users";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {

        userRepository.deleteById(id);

        return "redirect:/users";
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
        html.append("<tr><th>ID</th><th>Name</th><th>Age</th><th>Action</th></tr>");

        userRepository.findAll().forEach(user -> {
            html.append("<tr>")
                    .append("<td>").append(user.getId()).append("</td>")
                    .append("<td>").append(user.getName()).append("</td>")
                    .append("<td>").append(user.getAge()).append("</td>")
                    .append("<td>")
                    .append("<a href='/edit-user?id=").append(user.getId()).append("'>Edit</a> ")
                    .append("<a href='/delete-user?id=").append(user.getId()).append("'>Delete</a>")
                    .append("</td>")
                    .append("</tr>");
        });

        html.append("</table>");
        html.append("</body></html>");

        return html.toString();
    }
}
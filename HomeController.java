package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/kill-query")
    public String killQuery() {
        return "kill-query"; // Assuming you have a template named kill-query.html
    }

    @GetMapping("/log-aggregator")
    public String logAggregator() {
        return "log-aggregator"; // Assuming you have a template named log-aggregator.html
    }

    @GetMapping("/logout")
    public String logout() {
        // Implement your logout logic here
        return "redirect:/login"; // Assuming you have a login page
    }
}

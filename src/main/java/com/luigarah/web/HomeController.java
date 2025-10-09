package com.luigarah.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // Encaminha para a UI do Swagger
        return "forward:/swagger-ui/index.html";
    }
}
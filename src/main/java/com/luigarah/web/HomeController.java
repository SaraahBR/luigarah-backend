package com.luigarah.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Redireciona automaticamente para o Swagger UI
        return "redirect:/swagger-ui/index.html";
    }
}

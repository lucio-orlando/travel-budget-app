package ch.lucio_orlando.travel_budget_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/404")
    public String error404() {
        return "error/404";
    }

    @GetMapping("/400")
    public String error400() {
        return "error/400";
    }
}
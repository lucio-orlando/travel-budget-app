package ch.lucio_orlando.travel_budget_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class InfoController {

    @GetMapping("/info")
    public String info() {
        return "info";
    }
}
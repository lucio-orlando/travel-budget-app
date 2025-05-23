package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.models.Statistic;
import ch.lucio_orlando.travel_budget_app.services.StatisticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) throws JsonProcessingException {
        addCategoryStats(model);
        addWeeklyTotalStats(model);

        return "statistics/overview";
    }

    private void addCategoryStats(Model model) throws JsonProcessingException {
        List<Statistic> categoryStats = statisticsService.getCategoryStats();

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("categoryStats", mapper.writeValueAsString(categoryStats));
    }

    private void addWeeklyTotalStats(Model model) throws JsonProcessingException {
        List<Statistic> weeklyTotals = statisticsService.getWeeklyTotals();

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("weeklyStats", mapper.writeValueAsString(weeklyTotals));
    }
}
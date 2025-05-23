package ch.lucio_orlando.travel_budget_app.models;

import java.util.List;

public record DailyLineStatistic(List<Statistic> cumulativeBudget, List<Statistic> cumulativeSpent) {}

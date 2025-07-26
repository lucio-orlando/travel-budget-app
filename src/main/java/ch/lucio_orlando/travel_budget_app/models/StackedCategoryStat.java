package ch.lucio_orlando.travel_budget_app.models;

import java.util.List;

public record StackedCategoryStat(String parent, List<Statistic> children) {}


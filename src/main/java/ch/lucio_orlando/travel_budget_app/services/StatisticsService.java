package ch.lucio_orlando.travel_budget_app.services;

import ch.lucio_orlando.travel_budget_app.models.DailyLineStatistic;
import ch.lucio_orlando.travel_budget_app.models.Expense;
import ch.lucio_orlando.travel_budget_app.models.Statistic;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final CategoryService categoryService;
    private final ExpenseService expenseService;

    public StatisticsService(CategoryService categoryService, ExpenseService expenseService) {
        this.categoryService = categoryService;
        this.expenseService = expenseService;
    }

    public List<Statistic> getCategoryStats() {
        return categoryService.getCategories().stream()
            .filter(category -> category.getExpenses() != null && !category.getExpenses().isEmpty())
            .map(category -> new Statistic(
                category.getName(),
                category.getExpenses().stream()
                        .mapToDouble(Expense::getAmountCHF)
                        .sum()
            ))
            .sorted(Comparator.comparingDouble(Statistic::total).reversed())
            .toList();
    }

    public List<Statistic> getWeeklyTotals() {
        return expenseService.getExpenses().stream()
            .filter(e -> e.getDate() != null)
            .collect(Collectors.groupingBy(
                e -> {
                    LocalDate localDate = new java.sql.Date(e.getDate().getTime()).toLocalDate();
                    WeekFields weekFields = WeekFields.ISO; // or use Locale.GERMANY for ISO weeks
                    int week = localDate.get(weekFields.weekOfWeekBasedYear());
                    int year = localDate.get(weekFields.weekBasedYear());
                    return year + "-W" + String.format("%02d", week); // e.g., "2025-W18"
                },
                TreeMap::new,
                Collectors.summingDouble(Expense::getAmountCHF)
            ))
            .entrySet().stream()
            .map(entry -> new Statistic(entry.getKey(), entry.getValue()))
            .toList();
    }

    public DailyLineStatistic getCumulativeBudgetVsSpent(Trip trip) {
        LocalDate start = convertDateToLocalDate(trip.getDate());
        LocalDate end = trip.getEndDate() != null
            ? convertDateToLocalDate(trip.getEndDate()).isBefore(LocalDate.now())
            ? convertDateToLocalDate(trip.getEndDate())
            : LocalDate.now()
            : LocalDate.now();

        // Group expenses by date
        Map<LocalDate, Double> expensesPerDay = trip.getRecursiveExpenses(trip).stream()
            .filter(e -> e.getDate() != null)
            .collect(Collectors.groupingBy(
                e -> convertDateToLocalDate(e.getDate()),
                Collectors.summingDouble(Expense::getAmountCHF)
            ));

        List<Statistic> budget = new ArrayList<>();
        List<Statistic> spent = new ArrayList<>();

        double cumulativeBudget = 0;
        double cumulativeSpent = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            cumulativeBudget += trip.getDailyBudgetCHF();
            cumulativeSpent += expensesPerDay.getOrDefault(date, 0.0);

            String dateStr = date.toString(); // "yyyy-MM-dd"
            budget.add(new Statistic(dateStr, cumulativeBudget));
            spent.add(new Statistic(dateStr, cumulativeSpent));
        }

        return new DailyLineStatistic(budget, spent);
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
}

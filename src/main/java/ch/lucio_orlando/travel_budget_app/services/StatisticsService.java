package ch.lucio_orlando.travel_budget_app.services;

import ch.lucio_orlando.travel_budget_app.models.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public List<StackedCategoryStat> getStackedCategoryStats() {
        return categoryService.getCategories().stream()
            .filter(category -> category.getParentCategory() == null)
            .map(parent -> {
                List<Statistic> children = parent.getChildren().stream()
                    .filter(child -> child.getExpenses() != null && !child.getExpenses().isEmpty())
                    .map(child -> new Statistic(
                        child.getName(),
                        child.getExpenses().stream().mapToDouble(Expense::getAmountCHF).sum()
                    ))
                    .toList();

                // If no children but parent has expenses, treat it as a "flat" bar
                if (children.isEmpty() && parent.getExpenses() != null && !parent.getExpenses().isEmpty()) {
                    double total = parent.getExpenses().stream().mapToDouble(Expense::getAmountCHF).sum();
                    children = List.of(new Statistic(parent.getName(), total));
                }

                return new StackedCategoryStat(parent.getName(), children);
            })
            .filter(stat -> !stat.children().isEmpty()) // only keep those that will produce a bar
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

    public BigDecimal averagePerUnit(List<Expense> expenses, String keyword, boolean ignoreCounter) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCounter = 0;

        for (Expense e : expenses) {
            String fullName = e.getCategory().getFullName();

            if (fullName != null && fullName.toLowerCase().contains(keyword.toLowerCase())) {
                Integer counter = e.getCounter();
                boolean hasValidCounter = counter != null && counter > 0;

                if (ignoreCounter || hasValidCounter) {
                    totalAmount = totalAmount.add(BigDecimal.valueOf(e.getAmountCHF()));

                    if (ignoreCounter) {
                        totalCounter += hasValidCounter ? counter : 1;
                    } else {
                        totalCounter += counter;
                    }
                }
            }
        }


        return totalCounter > 0
            ? totalAmount.divide(BigDecimal.valueOf(totalCounter), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    }


    private LocalDate convertDateToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
}

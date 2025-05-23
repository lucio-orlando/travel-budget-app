package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Trip extends TripComponent {

    @OneToMany(mappedBy = "parentTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripComponent> components = new ArrayList<>();

    private Class<? extends TripComponent> componentType = null;

    private String image;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private double dailyBudgetCHF;

    public Trip(String name, Date date, Date endDate) {
        super(name, date);
        this.endDate = endDate;
    }

    public Trip() {}

    //<editor-fold desc="Getters and Setters">
    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateFormatted() {
        LocalDate localDate = ((java.sql.Date) endDate).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return localDate.format(formatter);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getDailyBudgetCHF() {
        return dailyBudgetCHF;
    }

    public void setDailyBudgetCHF(double dailyBudgetCHF) {
        this.dailyBudgetCHF = dailyBudgetCHF;
    }

    public List<TripComponent> getComponents() {
        return components;
    }

    public void addComponent(TripComponent component) {
        checkAndSetComponentType(component);

        components.add(component);
    }

    public void checkAndSetComponentType(TripComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        if (componentType != null && !componentType.isInstance(component)) {
            throw new IllegalArgumentException("Invalid component type");
        }

        if (componentType == null) {
            componentType = component.getClass();
        }
    }

    public void removeComponent(TripComponent component) {
        components.remove(component);
        component.setParentTrip(null);
    }

    public void setComponents(List<TripComponent> components) {
        this.components = components;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    //</editor-fold>

    @Override
    public double getAmount() {
        return components.stream().mapToDouble(TripComponent::getAmount).sum();
    }

    public double getAmountCHF() {
        return components.stream().mapToDouble(TripComponent::getAmountCHF).sum();
    }

    public double getDailyAverageCHF() {
        List<Expense> expenses = new ArrayList<>();

        if (isSubTripMode()) {
            expenses = getRecursiveExpenses(this);
        }
        if (isExpenseMode()) {
            expenses = components.stream()
                .filter(component -> component instanceof Expense)
                .map(component -> (Expense) component)
                .toList();
        }

        if (expenses.isEmpty()) {
            return 0.0;
        }

        double total = expenses.stream()
            .mapToDouble(TripComponent::getAmountCHF)
            .sum();

        // Calculate the full duration of the trip
        LocalDate startDate = ((java.sql.Date) date).toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate end = endDate != null ? ((java.sql.Date) endDate).toLocalDate() : today;

        // If the trip is ongoing, limit the end date to today
        if (end.isAfter(today)) {
            end = today;
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, end) + 1;

        if (totalDays <= 0) {
            return 0.0;
        }

        return total / totalDays;
    }

    public List<Expense> getRecursiveExpenses(Trip trip) {
        List<Expense> result = new ArrayList<>();

        for (TripComponent component : trip.getComponents()) {
            if (component instanceof Expense expense) {
                result.add(expense);
            } else if (component instanceof Trip subTrip) {
                result.addAll(getRecursiveExpenses(subTrip));
            }
        }

        return result;
    }

    public boolean isExpenseMode() {
        return componentType == Expense.class;
    }

    public boolean isSubTripMode() {
        return componentType == Trip.class;
    }

    public int getRemainingDays() {
        return (int) ((endDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
    }

    public boolean isActive() {
        if (date.getTime() > new Date().getTime()) {
            return false;
        }
        return endDate.getTime() >= new Date().getTime();
    }

    public void resetComponentType() {
        componentType = null;
    }
}

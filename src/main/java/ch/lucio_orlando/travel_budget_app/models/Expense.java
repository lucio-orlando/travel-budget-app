package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Expense extends TripComponent {

    private double amount;

    private double amountCHF;

    @ManyToOne(targetEntity = Category.class)
    @JoinColumn
    private Category category;

    private String description;

    public Expense(String name, int amount, Category category, Date date, String description) {
        super(name, date);

        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public Expense() {}

    //<editor-fold desc="Getters and Setters">
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountCHF() {
        return amountCHF;
    }

    public void setAmountCHF(double amountCHF) {
        this.amountCHF = amountCHF;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //</editor-fold>
}

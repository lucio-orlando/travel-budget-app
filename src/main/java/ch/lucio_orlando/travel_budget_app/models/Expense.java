package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Expense extends TripComponent {

    private double amount;

    private double amountCHF;

    private double conversionRate;

    @ManyToOne(targetEntity = Category.class)
    @JoinColumn
    private Category category;

    public Expense(String name, double amount, Category category, Date date, Currency currency) {
        super(name, date, currency);

        this.amount = amount;
        this.category = category;
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

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    //</editor-fold>
}

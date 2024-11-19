package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Expense extends TripComponent {

    private int amount;

    private String category;

    private String description;

    public Expense(String name, int amount, String category, Date date, String description) {
        super(name, date);

        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public Expense() {}

    //<editor-fold desc="Getters and Setters">
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

@Entity
public class Trip extends TripComponent {

    @OneToMany(mappedBy = "parentTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripComponent> components = new ArrayList<>();

    private Class<? extends TripComponent> componentType = null;

    private String image;

    private Currency currency;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Trip(String name, Date date, Date endDate) {
        super(name, date);
        this.endDate = endDate;
    }

    public Trip() {}

    //<editor-fold desc="Getters and Setters">
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<TripComponent> getComponents() {
        return components;
    }

    public void addComponent(TripComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        if (componentType != null && !componentType.isInstance(component)) {
            throw new IllegalArgumentException("Invalid component type");
        }

        if (componentType == null) {
            componentType = component.getClass();
        }

        components.add(component);
        component.setParentTrip(this);
    }

    public void removeComponent(TripComponent component) {
        components.remove(component);
        component.setParentTrip(null);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    //</editor-fold>

    @Override
    public int getAmount() {
        return components.stream().mapToInt(TripComponent::getAmount).sum();
    }

    public boolean isExpenseMode() {
        return componentType == Expense.class;
    }

    public boolean isSubTripMode() {
        return componentType == Trip.class;
    }
}

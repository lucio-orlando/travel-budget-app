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
    public int getAmount() {
        return components.stream().mapToInt(TripComponent::getAmount).sum();
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

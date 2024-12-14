package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class TripComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn
    protected Trip parentTrip;

    @Column(nullable = false)
    protected String name;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    protected Date date;

    @ManyToOne(targetEntity = Currency.class)
    @JoinColumn
    private Currency currency;

    public TripComponent(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public TripComponent() {}

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParentTrip(Trip parentTrip) {
        this.parentTrip = parentTrip;
    }

    public Trip getParentTrip() {
        return parentTrip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormatted() {
        LocalDate localDate = ((java.sql.Date) date).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.y");
        return localDate.format(formatter);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    //</editor-fold>

    public boolean isParentTrip() {
        return this.parentTrip == null;
    }

    public abstract int getAmount();
}

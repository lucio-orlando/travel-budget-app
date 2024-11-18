package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class TripComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Trip parentTrip;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date date;

    public TripComponent(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public TripComponent() {}

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
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

    public void setDate(Date date) {
        this.date = date;
    }
    //</editor-fold>

    public boolean isParentTrip() {
        return this.parentTrip == null;
    }

    public abstract int getAmount();
}

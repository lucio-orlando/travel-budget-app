package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Trip {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "parent_trip_id")
    private Trip parentTrip;

    @OneToMany(mappedBy = "parentTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> childTrips = new ArrayList<>();

    public Trip(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trip() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Trip getParentTrip() {
        return parentTrip;
    }

    public void setParentTrip(Trip parentTrip) {
        this.parentTrip = parentTrip;
    }

    public List<Trip> getChildTrips() {
        return childTrips;
    }

    public void addChildTrip(Trip childTrip) {
        if (this.parentTrip == null) { // Only parents can have children
            childTrips.add(childTrip);
            childTrip.setParentTrip(this);
        } else {
            throw new IllegalStateException("Child trips cannot have their own children.");
        }
    }

    public void removeChildTrip(Trip childTrip) {
        childTrips.remove(childTrip);
        childTrip.setParentTrip(null);
    }

    public boolean isParent() {
        return this.parentTrip == null;
    }
}

package ch.lucio_orlando.travel_budget_app.models;

import jakarta.persistence.*;

@Entity
public class Currency {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Currency() {}

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String country) {
        this.name = country;
    }
    //</editor-fold>
}

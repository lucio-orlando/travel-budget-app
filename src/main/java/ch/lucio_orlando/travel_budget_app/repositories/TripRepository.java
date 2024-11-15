package ch.lucio_orlando.travel_budget_app.repositories;

import ch.lucio_orlando.travel_budget_app.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

}

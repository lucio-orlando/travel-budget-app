package ch.lucio_orlando.travel_budget_app.services;

import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public void saveTrip(Trip trip) {
        tripRepository.save(trip);
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    public void checkComponentType(Trip trip) {
        if (trip == null) return;

        if (trip.getComponents().isEmpty()) {
            trip.resetComponentType();
        }

        this.saveTrip(trip);
    }
}
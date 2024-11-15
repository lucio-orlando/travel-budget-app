package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping
    public String getTrips(Model model) {
        List<Trip> trips = tripService.getTrips();
        model.addAttribute("trips", trips);
        return "trip/list";
    }

    @GetMapping({"/new", "/edit/{id}"})
    public String showTripForm(@PathVariable(required = false) Long id, Model model) {
        Trip trip = (id != null) ? tripService.getTripById(id).orElse(null) : new Trip();

        if (trip == null) return "redirect:/trips";

        model.addAttribute("trip", trip);
        return "trip/create-edit";
    }

    @PostMapping
    public String saveTrip(@ModelAttribute Trip trip, @RequestParam String startDate, @RequestParam(required = false) String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            trip.setStartDate(dateFormat.parse(startDate));

            if (endDate != null && !endDate.isEmpty()) {
                trip.setEndDate(dateFormat.parse(endDate));
            }

            tripService.saveTrip(trip);
            return "redirect:/trips";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return "redirect:/trips";
    }
}
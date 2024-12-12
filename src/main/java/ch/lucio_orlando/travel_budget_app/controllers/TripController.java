package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.api.unsplash.services.UnsplashApiService;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private UnsplashApiService unsplashApiService;

    @GetMapping({"", "/", "/trip"})
    public String overview(Model model) {
        List<Trip> trips = tripService.getTrips().stream().filter(trip -> trip.getParentTrip() == null).toList();
        model.addAttribute("trips", trips);
        return "trip/list";
    }

    @GetMapping("/trip/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Trip trip = tripService.getTripById(id).orElse(null);

        if (trip == null) return "redirect:/trip";

        model.addAttribute("trip", trip);
        return "trip/detail";
    }

    @GetMapping({"/trip/edit/{id}"})
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) return "redirect:/trip";
        Trip trip = tripService.getTripById(id).orElse(null);
        if (trip == null) return "redirect:/trip";

        model.addAttribute("trip", trip);
        return "trip/create-edit";
    }

    @GetMapping({"/trip/new/{id}", "/trip/new"})
    public String newForm(@PathVariable(required = false) Long id, Model model) {
        Trip trip = new Trip();
        if (id != null) {
            tripService.getTripById(id).ifPresent(trip::setParentTrip);
        }
        model.addAttribute("trip", trip);
        return "trip/create-edit";
    }

    @PostMapping("/trip")
    public String save(@ModelAttribute Trip trip, @RequestParam String date, @RequestParam(required = false) String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            trip.setDate(dateFormat.parse(date));

            if (endDate != null && !endDate.isEmpty()) {
                trip.setEndDate(dateFormat.parse(endDate));
            }

            trip.setImage(unsplashApiService.getPhotoUrl(trip.getName()));

            if (trip.getParentTrip() != null) {
                trip.getParentTrip().addComponent(trip);
            }

            tripService.saveTrip(trip);
            return "redirect:/trip";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/trip/delete/{id}")
    public String delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return "redirect:/trip";
    }
}
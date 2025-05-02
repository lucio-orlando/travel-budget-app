package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.api.unsplash.services.UnsplashApiService;
import ch.lucio_orlando.travel_budget_app.exceptions.InvalidDataException;
import ch.lucio_orlando.travel_budget_app.exceptions.ResourceNotFoundException;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Expense;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.models.TripComponent;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class TripController {

    private final TripService tripService;
    private final CurrencyService currencyService;
    private final UnsplashApiService unsplashApiService;

    public TripController(TripService tripService, CurrencyService currencyService, UnsplashApiService unsplashApiService) {
        this.tripService = tripService;
        this.currencyService = currencyService;
        this.unsplashApiService = unsplashApiService;
    }

    @GetMapping({"", "/", "/trip"})
    public String overview(Model model) {
        List<Trip> trips = tripService.getTrips().stream()
            .filter(trip -> trip.getParentTrip() == null)
            .sorted(Comparator.comparing(Trip::getDate).reversed())
            .toList();
        model.addAttribute("trips", trips);
        return "trip/list";
    }

    @GetMapping("/trip/{id}")
    public String detail(@PathVariable Long id, Model model) {
        if (id == null) throw new InvalidDataException("Trip ID is null");

        Trip trip = tripService.getTripById(id).orElse(null);

        if (trip == null) throw new ResourceNotFoundException("Trip with ID " + id + " not found");

        Map<String, List<Expense>> expensesByDate = trip.getComponents().stream()
            .filter(e -> e instanceof Expense)
            .map(e -> (Expense) e)
            .collect(Collectors.groupingBy(
                TripComponent::getDateFormatted, // format "02.05.2025"
                () -> new TreeMap<>(Comparator.reverseOrder()),              // keeps it ordered
                Collectors.toList()
            ));

        model.addAttribute("expensesByDate", expensesByDate);


        model.addAttribute("trip", trip);
        return "trip/detail";
    }

    @GetMapping({"/trip/edit/{id}"})
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) throw new InvalidDataException("Trip ID is null");

        Trip trip = tripService.getTripById(id).orElse(null);

        if (trip == null) throw new ResourceNotFoundException("Trip with ID " + id + " not found");

        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("trip", trip);
        model.addAttribute("currencies", currencies);
        model.addAttribute("errorMessage", null);
        return "trip/create-edit";
    }

    @GetMapping({"/trip/new/{id}", "/trip/new"})
    public String newForm(@PathVariable(required = false) Long id, Model model) {
        // TODO: edit needs to be fixed -> detached entity error

        Trip trip = new Trip();
        Trip parentTrip = null;
        if (id != null) {
            parentTrip = tripService.getTripById(id).orElse(null);
            trip.setParentTrip(parentTrip);
        }

        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("trip", parentTrip);
        model.addAttribute("draftTrip", trip);
        model.addAttribute("currencies", currencies);
        model.addAttribute("errorMessage", null);
        return "trip/create-edit";
    }

    @PostMapping("/trip")
    public String save(
        @ModelAttribute Trip trip,
        @RequestParam String date,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Long currencyId,
        Model model
    ) {
        if (trip == null) throw new InvalidDataException("Trip is null");

        if (trip.getName() == null || trip.getName().isEmpty() || date == null || date.isEmpty()) {
            List<Currency> currencies = currencyService.getCurrencies();
            model.addAttribute("trip", trip);
            model.addAttribute("currencies", currencies);
            model.addAttribute("errorMessage", "Error: name and date are required.");
            return "trip/create-edit";
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            trip.setDate(dateFormat.parse(date));

            if (endDate != null && !endDate.isEmpty()) {
                trip.setEndDate(dateFormat.parse(endDate));
            }

            if (currencyId != null) {
                trip.setCurrency(currencyService.getCurrencyById(currencyId));
            }

            trip.setImage(unsplashApiService.getPhotoUrl(trip.getName()));

            if (trip.getParentTrip() != null) {
                trip.getParentTrip().addComponent(trip);
            }

            tripService.saveTrip(trip);
            return redirect("/trip/" + trip.getId());
        } catch (Exception e) {
            throw new InvalidDataException("Error saving trip: " + e.getMessage(), e);
        }
    }

    @GetMapping({"/trip/delete/{id}", "/trip/delete/{id}/{parentTripId}"})
    public String delete(@PathVariable Long id, @PathVariable(required = false) Long parentTripId) {
        tripService.deleteTrip(id);
        if (parentTripId != null) {
            tripService.checkComponentType(tripService.getTripById(parentTripId).orElse(null));
            return redirect("/trip/" + parentTripId);
        }
        return redirect("/trip");
    }

    private String redirect(String url) {
        return "redirect:" + url;
    }
}
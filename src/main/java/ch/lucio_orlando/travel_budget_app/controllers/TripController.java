package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.api.unsplash.services.UnsplashApiService;
import ch.lucio_orlando.travel_budget_app.exceptions.InvalidDataException;
import ch.lucio_orlando.travel_budget_app.exceptions.ResourceNotFoundException;
import ch.lucio_orlando.travel_budget_app.models.*;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.StatisticsService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final StatisticsService statisticsService;
    private final CurrencyService currencyService;
    private final UnsplashApiService unsplashApiService;

    public TripController(TripService tripService, StatisticsService statisticsService, CurrencyService currencyService, UnsplashApiService unsplashApiService) {
        this.tripService = tripService;
        this.statisticsService = statisticsService;
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
    public String detail(@PathVariable Long id, Model model) throws JsonProcessingException {
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

        //data for the chart
        ObjectMapper mapper = new ObjectMapper();
        DailyLineStatistic data = statisticsService.getCumulativeBudgetVsSpent(trip);
        model.addAttribute("budgetLine", mapper.writeValueAsString(data.cumulativeBudget()));
        model.addAttribute("spentLine", mapper.writeValueAsString(data.cumulativeSpent()));

        model.addAttribute("expensesByDate", expensesByDate);
        model.addAttribute("trip", trip);
        return "trip/detail";
    }

    @GetMapping({"/trip/edit/{id}"})
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) throw new InvalidDataException("Trip ID is null");

        Trip trip = tripService.getTripById(id).orElse(null);

        if (trip == null) throw new ResourceNotFoundException("Trip with ID " + id + " not found");

        prepareForm(model, trip.getParentTrip(), trip, null);
        return "trip/create-edit";
    }

    @GetMapping({"/trip/new/{id}", "/trip/new"})
    public String newForm(@PathVariable(required = false) Long id, Model model) {
        Trip trip = new Trip();
        Trip parentTrip = null;
        if (id != null) {
            parentTrip = tripService.getTripById(id).orElse(null);
            trip.setParentTrip(parentTrip);
        }

        prepareForm(model, parentTrip, trip, null);
        return "trip/create-edit";
    }

    @PostMapping("/trip")
    public String save(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) Long parentTrip,
        @RequestParam(required = false) Long currency,
        @RequestParam(required = false) Double dailyBudgetCHF,
        @RequestParam String name,
        @RequestParam String date,
        @RequestParam(required = false) String endDate,
        Model model
    ) {
        Trip trip;

        if (id != null) {
            trip = tripService.getTripById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with ID " + id + " not found"));
        } else {
            trip = new Trip();
        }

        if (name == null || name.isEmpty() || date == null || date.isEmpty()) {
            prepareForm(model, trip.getParentTrip(), trip, "Error: name and date are required.");
            return "trip/create-edit";
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            trip.setDate(dateFormat.parse(date));
            trip.setName(name);

            if (endDate != null && !endDate.isEmpty()) {
                trip.setEndDate(dateFormat.parse(endDate));
            }

            if (currency != null) {
                trip.setCurrency(currencyService.getCurrencyById(currency));
            }

            trip.setImage(unsplashApiService.getPhotoUrl(trip.getName()));

            trip.setDailyBudgetCHF(dailyBudgetCHF != null ? dailyBudgetCHF : 0.0);
            if (parentTrip != null) {
                Trip parentTripObj = tripService.getTripById(parentTrip)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent trip with ID " + parentTrip + " not found"));
                trip.setParentTrip(tripService.getTripById(parentTrip).orElse(null));
                parentTripObj.addComponent(trip);
            } else {
                trip.setParentTrip(null);
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

    private void prepareForm(Model model, Trip parentTrip, Trip trip, String errorMessage) {
        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("trip", parentTrip);
        model.addAttribute("draftTrip", trip);
        model.addAttribute("currencies", currencies);
        model.addAttribute("errorMessage", errorMessage);
    }
}
package ch.lucio_orlando.travel_budget_app.controller;

import ch.lucio_orlando.travel_budget_app.controllers.TripController;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TripControllerTest {

    @Mock
    private TripService tripService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private TripController tripController;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void tripListPageTest() {
        List<Trip> mockTrips = new ArrayList<>();
        mockTrips.add(new Trip());
        mockTrips.add(new Trip());
        when(tripService.getTrips()).thenReturn(mockTrips);

        String viewName = tripController.overview(model);

        assertEquals("trip/list", viewName);
        verify(model).addAttribute("trips", mockTrips);
    }

    @Test
    void tripAddFormPageTest() {
        List<Currency> mockCurrencies = new ArrayList<>();
        when(currencyService.getCurrencies()).thenReturn(mockCurrencies);
        String viewNameWithId = tripController.newForm(1L, model);
        String viewNameWithoutId = tripController.newForm(null, model);

        assertEquals("trip/create-edit", viewNameWithId);
        assertEquals("trip/create-edit", viewNameWithoutId);
    }

    @Test
    void tripDetailPageTest() {
        Trip mockTrip = new Trip();
        when(tripService.getTripById(1L)).thenReturn(java.util.Optional.of(mockTrip));

        String viewName = tripController.detail(1L, model);

        assertEquals("trip/detail", viewName);
        verify(model).addAttribute("trip", mockTrip);
    }

    @Test
    void tripDetailPageNotFoundTest() {
        when(tripService.getTripById(1L)).thenReturn(java.util.Optional.empty());

        String viewName = tripController.detail(1L, model);

        assertEquals("redirect:/404", viewName);
    }

    @Test
    void tripEditFormPageNotFoundTest() {
        when(tripService.getTripById(1L)).thenReturn(java.util.Optional.empty());

        String viewName = tripController.editForm(1L, model);

        assertEquals("redirect:/404", viewName);
    }

    @Test
    void tripEditFormPageTest() {
        Trip mockTrip = new Trip();
        when(tripService.getTripById(1L)).thenReturn(java.util.Optional.of(mockTrip));

        List<Currency> mockCurrencies = new ArrayList<>();
        when(currencyService.getCurrencies()).thenReturn(mockCurrencies);

        String viewName = tripController.editForm(1L, model);

        assertEquals("trip/create-edit", viewName);
        verify(model).addAttribute("trip", mockTrip);
        verify(model).addAttribute("currencies", mockCurrencies);
    }

    @Test
    void tripDeleteTest() {
        Long tripId = 1L;

        String viewName = tripController.delete(tripId, null);

        assertEquals("redirect:/trip", viewName);
        verify(tripService).deleteTrip(tripId);
    }
}

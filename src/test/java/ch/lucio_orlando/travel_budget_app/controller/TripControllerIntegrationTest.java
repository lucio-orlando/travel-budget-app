package ch.lucio_orlando.travel_budget_app.controller;

import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.repositories.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TripControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
    }

    @Test
    void tripAddIntegrationTest() throws Exception {
       mockMvc.
           perform(
               post("/trip")
                   .param("name", "Trip Test")
                   .param("date", "2025-01-01")
           )
           .andExpect(status().is3xxRedirection());

        assertEquals(1, tripRepository.count());
        Trip savedTrip = tripRepository.findAll().getFirst();
        assertEquals("Trip Test", savedTrip.getName());
        assertEquals("01.01.2025", savedTrip.getDateFormatted());
    }

    @Test
    void tripsShowIntegrationTest() throws Exception {
        Trip trip = new Trip("Test Trip", new Date(), null);
        tripRepository.save(trip);

        mockMvc.perform(get("/trip"))
            .andExpect(status().isOk())
            .andExpect(view().name("trip/list"))
            .andExpect(model().attributeExists("trips"))
            .andExpect(model().attribute("trips", hasSize(1)))
            .andExpect(model().attribute(
                "trips",
                contains(
                    hasProperty(
                        "name",
                        is("Test Trip")
                    )
                )
            ));
    }
}

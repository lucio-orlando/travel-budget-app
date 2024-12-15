package ch.lucio_orlando.travel_budget_app.loader;

import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DemoDataLoader {

    @Autowired
    private TripService tripService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrencyService currencyService;

    @Value("${app.load-demo-data:false}")
    private boolean loadDemoData;

    @Value("${app.allow-api-calls}")
    private boolean allowApiCalls;

    @PostConstruct
    public void loadDemoData() {
        if (loadDemoData) {
            if (!allowApiCalls) {
                loadDemoCurrency();
            }

            loadDemoTrips();
            loadDemoCategories();

        }
    }

    private void loadDemoTrips() {
        if (tripService.getTrips().isEmpty()) {
            Trip asiaTrip = createTrip(
                "Asia-Backpacking",
                "https://images.unsplash.com/photo-1469487885741-33b975dd5bbc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTE2ODd8&ixlib=rb-4.0.3&q=80&w=1080",
                0,
                14,
                null
            );
            Trip vietnamTrip = createTrip(
                "Thailand",
                "https://images.unsplash.com/photo-1578039821447-e3884b8472a0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzQwMTY3ODJ8&ixlib=rb-4.0.3&q=80&w=1080",
                0,
                7,
                "THB"
            );
            asiaTrip.addComponent(vietnamTrip);

            Trip centralAmericaTrip = createTrip(
                "Central America",
                "https://images.unsplash.com/photo-1704694214588-24f4bae4757b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTIzNzR8&ixlib=rb-4.0.3&q=80&w=1080",
                -14,
                -7,
                "CRC"
            );

            tripService.saveTrip(asiaTrip);
            tripService.saveTrip(vietnamTrip);
            tripService.saveTrip(centralAmericaTrip);
        }
    }

    private Trip createTrip(String name, String image, int startDayOffset, int durationDays, String currencyCode) {
        Trip trip = new Trip();
        trip.setName(name);
        trip.setDate(randomDateFromNow(startDayOffset));
        trip.setEndDate(randomDateFromNow(startDayOffset + durationDays));
        trip.setImage(image);
        trip.setCurrency(currencyService.getCurrencyByCode(currencyCode));
        return trip;
    }

    private Date randomDateFromNow(int offsetDays) {
        long currentTime = System.currentTimeMillis();
        long offsetMillis = offsetDays * 24L * 60 * 60 * 1000;
        long randomMillis = ThreadLocalRandom.current().nextLong(offsetMillis - 2 * 24 * 60 * 60 * 1000, offsetMillis + 2 * 24 * 60 * 60 * 1000);
        return new Date(currentTime + randomMillis);
    }

    private void loadDemoCategories() {
        if (categoryService.getCategories().isEmpty()) {
            categoryService.saveCategory(new Category("Accommodation", "#FF0000"));
            categoryService.saveCategory(new Category("Food", "#00FF00"));
            categoryService.saveCategory(new Category("Transport", "#0000FF"));
            categoryService.saveCategory(new Category("Activities", "#FFFF00"));
        }
    }

    private void loadDemoCurrency() {
        if (currencyService.getCurrencies().isEmpty()) {
            currencyService.saveCurrency(new Currency("USD", "US Dollar"));
            currencyService.saveCurrency(new Currency("EUR", "Euro"));
            currencyService.saveCurrency(new Currency("VND", "Vietnamese Dong"));
            currencyService.saveCurrency(new Currency("CRC", "Costa Rican Colón"));
            currencyService.saveCurrency(new Currency("THB", "Thai Baht"));
            currencyService.saveCurrency(new Currency("CHF", "Swiss Franc"));
        }
    }

}

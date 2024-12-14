package ch.lucio_orlando.travel_budget_app.loader;

import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.repositories.CategoryRepository;
import ch.lucio_orlando.travel_budget_app.repositories.CurrencyRepository;
import ch.lucio_orlando.travel_budget_app.repositories.TripRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DemoDataLoader {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${app.load-demo-data:false}")
    private boolean loadDemoData;

    @PostConstruct
    public void loadDemoData() {
        if (loadDemoData) {
            loadDemoTrips();
            loadDemoCategories();
            loadDemoCurrency();
        }
    }

    private void loadDemoTrips() {
        if (tripRepository.count() == 0) {
            Trip asiaTrip = createTrip(
                "Asia-Backpacking",
                "https://images.unsplash.com/photo-1469487885741-33b975dd5bbc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTE2ODd8&ixlib=rb-4.0.3&q=80&w=1080",
                0,
                14
            );
            Trip vietnamTrip = createTrip(
                "Vietnam",
                "https://images.unsplash.com/photo-1578039821447-e3884b8472a0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzQwMTY3ODJ8&ixlib=rb-4.0.3&q=80&w=1080",
                0,
                7
            );
            asiaTrip.addComponent(vietnamTrip);

            Trip centralAmericaTrip = createTrip(
                "Central America",
                "https://images.unsplash.com/photo-1704694214588-24f4bae4757b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTIzNzR8&ixlib=rb-4.0.3&q=80&w=1080",
                -14,
                -7
            );

            tripRepository.save(asiaTrip);
            tripRepository.save(vietnamTrip);
            tripRepository.save(centralAmericaTrip);
        }
    }

    private Trip createTrip(String name, String image, int startDayOffset, int durationDays) {
        Trip trip = new Trip();
        trip.setName(name);
        trip.setDate(randomDateFromNow(startDayOffset));
        trip.setEndDate(randomDateFromNow(startDayOffset + durationDays));
        trip.setImage(image);
        return trip;
    }

    private Date randomDateFromNow(int offsetDays) {
        long currentTime = System.currentTimeMillis();
        long offsetMillis = offsetDays * 24L * 60 * 60 * 1000;
        long randomMillis = ThreadLocalRandom.current().nextLong(offsetMillis - 2 * 24 * 60 * 60 * 1000, offsetMillis + 2 * 24 * 60 * 60 * 1000);
        return new Date(currentTime + randomMillis);
    }

    private void loadDemoCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Accommodation", "#FF0000"));
            categoryRepository.save(new Category("Food", "#00FF00"));
            categoryRepository.save(new Category("Transport", "#0000FF"));
            categoryRepository.save(new Category("Activities", "#FFFF00"));
        }
    }

    private void loadDemoCurrency() {
        if (currencyRepository.count() == 0) {
            currencyRepository.save(new Currency("USD", "US Dollar"));
            currencyRepository.save(new Currency("EUR", "Euro"));
            currencyRepository.save(new Currency("VND", "Vietnamese Dong"));
            currencyRepository.save(new Currency("CRC", "Costa Rican Colón"));
            currencyRepository.save(new Currency("THB", "Thai Baht"));
            currencyRepository.save(new Currency("CHF", "Swiss Franc"));
        }
    }

}

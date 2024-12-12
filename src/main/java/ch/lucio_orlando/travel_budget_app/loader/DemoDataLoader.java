package ch.lucio_orlando.travel_budget_app.loader;

import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.repositories.TripRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DemoDataLoader {

    @Autowired
    private TripRepository tripRepository;

    @Value("${app.load-demo-data:false}")
    private boolean loadDemoData;

    @PostConstruct
    public void loadDemoData() {
        if (loadDemoData) {
            loadDemoTrips();
        }
    }

    private void loadDemoTrips() {
        if (tripRepository.count() == 0) {
            Trip asiaTrip = getAsiaDemoTrip();
            Trip vietnamTrip = getVietnamDemoTrip();
            asiaTrip.addComponent(vietnamTrip);
            tripRepository.save(asiaTrip);
            tripRepository.save(vietnamTrip);
            tripRepository.save(getCentralAmericaDemoTrip());
        }
    }

    private Trip getAsiaDemoTrip() {
        Trip trip = new Trip();
        trip.setName("Asian Adventure");
        trip.setDate(new Date());
        trip.setEndDate(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000));
        trip.setImage("https://images.unsplash.com/photo-1469487885741-33b975dd5bbc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTE2ODd8&ixlib=rb-4.0.3&q=80&w=1080");
        return trip;
    }

    private Trip getVietnamDemoTrip() {
        Trip trip = new Trip();
        trip.setName("Vietnam");
        trip.setDate(new Date());
        trip.setEndDate(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000));
        trip.setImage("https://images.unsplash.com/photo-1578039821447-e3884b8472a0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzQwMTY3ODJ8&ixlib=rb-4.0.3&q=80&w=1080");
        return trip;
    }

    private Trip getCentralAmericaDemoTrip() {
        Trip trip = new Trip();
        trip.setName("Central America");
        trip.setDate(new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000));
        trip.setEndDate(new Date(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 100));
        trip.setImage("https://images.unsplash.com/photo-1704694214588-24f4bae4757b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2NzgyNTV8MHwxfHJhbmRvbXx8fHx8fHx8fDE3MzM3NTIzNzR8&ixlib=rb-4.0.3&q=80&w=1080");
        return trip;
    }
}

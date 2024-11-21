package ch.lucio_orlando.travel_budget_app.repositories;

import ch.lucio_orlando.travel_budget_app.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}

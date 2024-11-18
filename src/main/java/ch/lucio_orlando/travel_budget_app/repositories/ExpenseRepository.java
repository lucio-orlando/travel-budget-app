package ch.lucio_orlando.travel_budget_app.repositories;

import ch.lucio_orlando.travel_budget_app.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}

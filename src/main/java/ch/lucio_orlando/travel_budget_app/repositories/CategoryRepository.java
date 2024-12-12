package ch.lucio_orlando.travel_budget_app.repositories;

import ch.lucio_orlando.travel_budget_app.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

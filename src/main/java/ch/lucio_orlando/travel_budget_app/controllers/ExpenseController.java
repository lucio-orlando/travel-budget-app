package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.api.exchange_rate.services.ExchangeRateApiService;
import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Expense;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.ExpenseService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ExchangeRateApiService exchangeRateApiService;

    @Autowired
    private TripService tripService;

    @GetMapping({"/trip/{tripId}/expense", "/trip/{tripId}/expense/{id}"})
    public String form(@PathVariable String tripId, @PathVariable(required = false) Long id, Model model) {
        Expense expense = (id != null) ? expenseService.getExpenseById(id).orElse(null) : new Expense();
        Trip parentTrip = tripService.getTripById(Long.parseLong(tripId)).orElse(null);

        if (expense == null || parentTrip == null) {
            return "redirect:/trip/" + tripId;
        }

        expense.setParentTrip(parentTrip);

        List<Category> categories = categoryService.getCategories();
        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("categories", categories);
        model.addAttribute("currencies", currencies);
        model.addAttribute("expense", expense);
        return "expense/create-edit";
    }

    @PostMapping("/trip/{tripId}/expense")
    public String save(
        @PathVariable Long tripId,
        @ModelAttribute Expense expense,
        @RequestParam String date,
        @RequestParam(required = false) Long currencyId
    ) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            expense.setDate(dateFormat.parse(date));

            if (currencyId != null) {
                expense.setCurrency(currencyService.getCurrencyById(currencyId));
            }

            if (expense.getParentTrip() != null) {
                expense.getParentTrip().addComponent(expense);
            }

            expense.setAmountCHF(
                exchangeRateApiService.getExchangeAmount(
                    expense.getCurrency(),
                    currencyService.getCurrencyByCode("CHF"),
                    expense.getAmount()
                )
            );

            expenseService.saveExpense(expense);
            return "redirect:/trip/" + tripId;
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/trip/{tripId}/expense/{id}/delete")
    public String delete(@PathVariable Long tripId, @PathVariable Long id) {
        expenseService.deleteExpense(id);
        tripService.checkComponentType(tripService.getTripById(tripId).orElse(null));
        return "redirect:/trip/" + tripId;
    }
}
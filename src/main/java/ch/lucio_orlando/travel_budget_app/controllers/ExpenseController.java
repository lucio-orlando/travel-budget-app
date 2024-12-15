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
import java.util.Date;
import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;
    private final ExchangeRateApiService exchangeRateApiService;
    private final TripService tripService;

    public ExpenseController(ExpenseService expenseService, CategoryService categoryService, CurrencyService currencyService, ExchangeRateApiService exchangeRateApiService, TripService tripService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
        this.exchangeRateApiService = exchangeRateApiService;
        this.tripService = tripService;
    }

    @GetMapping({"/trip/{tripId}/expense", "/trip/{tripId}/expense/{id}"})
    public String form(@PathVariable String tripId, @PathVariable(required = false) Long id, Model model) {
        Trip parentTrip = tripService.getTripById(Long.parseLong(tripId)).orElse(null);
        if (parentTrip == null) return redirect("/404");

        Expense expense = (id != null) ? expenseService.getExpenseById(id).orElse(null) : new Expense("", 0, null, new Date(), parentTrip.getCurrency());

        if (expense == null) return redirect("/404");

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
            return redirect("/trip/" + tripId);
        } catch (Exception e) {
            return redirect("400");
        }
    }

    @GetMapping("/trip/{tripId}/expense/{id}/delete")
    public String delete(@PathVariable Long tripId, @PathVariable Long id) {
        expenseService.deleteExpense(id);
        tripService.checkComponentType(tripService.getTripById(tripId).orElse(null));
        return redirect("/trip/" + tripId);
    }

    private String redirect(String url) {
        return "redirect:" + url;
    }
}
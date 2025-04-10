package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.api.exchange_rate.services.ExchangeRateApiService;
import ch.lucio_orlando.travel_budget_app.exceptions.InvalidDataException;
import ch.lucio_orlando.travel_budget_app.exceptions.ResourceNotFoundException;
import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.models.Expense;
import ch.lucio_orlando.travel_budget_app.models.Trip;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import ch.lucio_orlando.travel_budget_app.services.ExpenseService;
import ch.lucio_orlando.travel_budget_app.services.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/trip")
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

    @GetMapping({"/{tripId}/expense", "/{tripId}/expense/{id}"})
    public String form(@PathVariable Long tripId, @PathVariable(required = false) Long id, Model model) {
        if (tripId == null) throw new InvalidDataException("Trip ID is null");

        Trip parentTrip = tripService.getTripById(tripId).orElse(null);
        if (parentTrip == null) throw new ResourceNotFoundException("Trip with ID " + tripId + " not found");

        Expense expense = (id != null) ?
            expenseService.getExpenseById(id).orElse(null) :
            new Expense("", 0, null, new Date(), parentTrip.getCurrency());

        if (expense == null) throw new ResourceNotFoundException("Expense with ID " + id + " not found");

        expense.setParentTrip(parentTrip);
        prepareFormModel(model, expense, null);

        return "expense/create-edit";
    }

    @PostMapping("/{tripId}/expense")
    public String save(
        @PathVariable Long tripId,
        @ModelAttribute Expense expense,
        @RequestParam String date,
        @RequestParam(required = false) Long currency,
        Model model
    ) {
        if (expense == null) throw new InvalidDataException("Expense is null");

        Currency selectedCurrency = (currency != null) ? currencyService.getCurrencyById(currency) : null;
        Trip parentTrip = tripService.getTripById(tripId).orElse(null);

        List<String> validationErrors = validateExpense(expense, date, selectedCurrency, parentTrip);
        if (!validationErrors.isEmpty()) {
            prepareFormModel(model, expense, String.join(" ", validationErrors));
            return "expense/create-edit";
        }

        try {
            assert parentTrip != null;
            prepareExpense(expense, date, selectedCurrency, parentTrip);

            tripService.saveTrip(parentTrip);

            return redirect("/trip/" + tripId);
        } catch (Exception e) {
            throw new InvalidDataException("Error saving expense: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{tripId}/expense/{id}/delete")
    public String delete(@PathVariable Long tripId, @PathVariable Long id) {
        expenseService.deleteExpense(id);
        tripService.checkComponentType(tripService.getTripById(tripId).orElse(null));
        return redirect("/trip/" + tripId);
    }

    /**
     * Redirects to the given URL.
     * @param url The URL to redirect to.
     * @return The redirect string.
     */
    private String redirect(String url) {
        return "redirect:" + url;
    }

    /**
     * Prepares and sets the properties of the expense object.
     * @param expense The expense object to prepare.
     * @param date The date string to parse.
     * @param currency The currency object to set.
     * @param parentTrip The parent trip object to link the expense to.
     * @throws ParseException If the date string cannot be parsed.
     */
    private void prepareExpense(Expense expense, String date, Currency currency, Trip parentTrip) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        expense.setDate(dateFormat.parse(date));
        expense.setCurrency(currency);

        double amountCHF = exchangeRateApiService.getExchangeAmount(
            currency,
            currencyService.getCurrencyByCode("CHF"),
            expense.getAmount()
        );
        expense.setAmountCHF(amountCHF);

        parentTrip.addComponent(expense); // links both ways
    }

    /**
     * Validates the expense object and its properties.
     * @param expense The expense object to validate.
     * @param date The date string to validate.
     * @param currency The currency object to validate.
     * @param parentTrip The parent trip object to validate.
     * @return A list of error messages if validation fails, otherwise an empty list.
     */
    private List<String> validateExpense(Expense expense, String date, Currency currency, Trip parentTrip) {
        List<String> errors = new ArrayList<>();

        if (expense == null) {
            errors.add("Expense is missing.");
        } else {
            if (expense.getCategory() == null) errors.add("Category is required.");
            if (expense.getAmount() <= 0) errors.add("Amount must be greater than 0.");
            if (expense.getName() == null || expense.getName().isEmpty()) errors.add("Name is required.");
        }

        if (currency == null) errors.add("Currency is required.");
        if (date == null || date.isEmpty()) errors.add("Date is required.");
        if (parentTrip == null) errors.add("Trip not found.");

        return errors;
    }

    /**
     * Prepares the model for the expense form with the given attributes.
     * @param model The model to prepare.
     * @param expense The expense object to set in the model.
     * @param errorMessage The error message to set in the model.
     */
    private void prepareFormModel(Model model, Expense expense, String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("expense", expense);
    }
}
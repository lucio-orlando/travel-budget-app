package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.models.Expense;
import ch.lucio_orlando.travel_budget_app.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping({"/trip/{tripId}/expense", "/trip/{tripId}/expense/edit/{id}"})
    public String form(@PathVariable String tripId, @PathVariable(required = false) Long id, Model model) {
        Expense expense = (id != null) ? expenseService.getExpenseById(id).orElse(null) : new Expense();

        if (expense == null) return "redirect:/trip/" + tripId;

        model.addAttribute("expense", expense);
        return "expense/create-edit";
    }

    @PostMapping("/trip/{tripId}/expense")
    public String save(@PathVariable Long tripId, @ModelAttribute Expense expense, @RequestParam String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            expense.setDate(dateFormat.parse(date));

            expenseService.saveExpense(expense);
            return "redirect:/trip/" + tripId;
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/trip/{tripId}/expense/delete/{id}")
    public String delete(@PathVariable Long tripId, @PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "redirect:/trip/" + tripId;
    }
}
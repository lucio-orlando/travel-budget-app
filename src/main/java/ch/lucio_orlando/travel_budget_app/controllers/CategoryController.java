package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/category"})
    public String overview(Model model) {
        List<Category> categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        return "category/list";
    }

    @GetMapping({"/category/edit/{id}"})
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) return redirect("/404");

        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category == null) return redirect("/404");

        model.addAttribute("category", category);
        return "category/create-edit";
    }

    @GetMapping({"/category/new"})
    public String newForm(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        return "category/create-edit";
    }

    @PostMapping("/category")
    public String save(@ModelAttribute Category category) {
        try {
            categoryService.saveCategory(category);
            return redirect("/category");
        } catch (Exception e) {
            return redirect("/400");
        }
    }

    @GetMapping({"/category/delete/{id}"})
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return redirect("/category");
    }

    private String redirect(String url) {
        return "redirect:" + url;
    }
}
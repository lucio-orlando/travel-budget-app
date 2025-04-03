package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String overview(Model model) {
        List<Category> categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        return "category/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) return redirect("/404");

        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category == null) return redirect("/404");

        model.addAttribute("category", category);
        model.addAttribute("errorMessage", null);
        return "category/create-edit";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("errorMessage", null);
        return "category/create-edit";
    }

    @PostMapping
    public String save(@ModelAttribute Category category, Model model) {
        if (category == null) return redirect("/400");

        if (category.getName() == null || category.getName().isEmpty() || category.getColor() == null) {
            model.addAttribute("category", category);
            model.addAttribute("errorMessage", "Error: name and color are required.");
            return "category/create-edit";
        }

        try {
            categoryService.saveCategory(category);
            return redirect("/category");
        } catch (Exception e) {
            return redirect("/400");
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return redirect("/category");
    }

    private String redirect(String url) {
        return "redirect:" + url;
    }
}
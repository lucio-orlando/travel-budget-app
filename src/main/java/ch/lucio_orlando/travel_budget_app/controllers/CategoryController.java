package ch.lucio_orlando.travel_budget_app.controllers;

import ch.lucio_orlando.travel_budget_app.exceptions.InvalidDataException;
import ch.lucio_orlando.travel_budget_app.exceptions.ResourceNotFoundException;
import ch.lucio_orlando.travel_budget_app.models.Category;
import ch.lucio_orlando.travel_budget_app.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Category> parentCategories = categories
            .stream()
            .filter(c -> c.getParentCategory() == null) // Only top-level categories
            .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
            .toList();

        Map<Category, List<Category>> groupedCategories = parentCategories.stream()
            .collect(Collectors.toMap(
                parent -> parent,
                parent -> categories.stream()
                    .filter(c -> parent.equals(c.getParentCategory()))
                    .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                    .toList(),
                (a, b) -> a,  // merge function, not needed here
                LinkedHashMap::new // preserve order
            ));

        model.addAttribute("groupedCategories", groupedCategories);
        return "category/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable() Long id, Model model) {
        if (id == null) throw new InvalidDataException("Category ID is null");

        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category == null) throw new ResourceNotFoundException("Category with ID " + id + " not found");

        return prepareCreateEditView(category, null, model);
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return prepareCreateEditView(new Category(), null, model);
    }

    @PostMapping
    public String save(@ModelAttribute Category category, Model model) {
        if (category == null) throw new InvalidDataException("Category ID is null");

        if (category.getName() == null || category.getName().isEmpty()) {
            return prepareCreateEditView(category, "Error: name is required.", model);
        }

        try {
            categoryService.saveCategory(category);
            return redirect("/category");
        } catch (Exception e) {
            throw new InvalidDataException("Error saving category: " + e.getMessage(), e);
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

    private String prepareCreateEditView(Category category, String errorMessage, Model model) {
        List<Category> categories = categoryService.getCategories()
            .stream()
            .filter(c -> c.getParentCategory() == null)
            .filter(c -> !c.getId().equals(category.getId())) // Exclude the current category if editing
            .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
            .toList();

        model.addAttribute("category", category);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("parentCategories", categories);
        return "category/create-edit";
    }
}
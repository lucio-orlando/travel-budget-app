package ch.lucio_orlando.travel_budget_app.e2e;

import ch.lucio_orlando.travel_budget_app.e2e.pages.CategoryPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryPageTest {
    private WebDriver driver;
    private CategoryPage categoryPage;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        categoryPage = new CategoryPage(driver, port);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void categoryAddValidationTest() {
        categoryPage.navigateTo();

        categoryPage.addNewCategory("", "#FF0000");
        String errorMessage = categoryPage.getErrorMessage();
        assertEquals("Error: name and color are required.", errorMessage);
    }

    @Test
    public void categoryEditTest() {
        categoryPage.navigateTo();

        String categoryName = "Transport";
        categoryPage.addNewCategory(categoryName, "#FF0000");

        assertTrue(categoryPage.isCategoryListed(categoryName), "Category was not added to the list.");

    }
}

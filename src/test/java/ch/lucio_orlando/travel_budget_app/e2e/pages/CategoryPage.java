package ch.lucio_orlando.travel_budget_app.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CategoryPage {
    private WebDriver driver;
    private int port;

    public CategoryPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;
    }

    public void navigateTo() {
        driver.get("http://localhost:" + port + "/category/new");
    }

    public void addNewCategory(String name, String color) {
        WebElement nameField = driver.findElement(By.id("name"));
        WebElement colorField = driver.findElement(By.id("color"));
        WebElement addButton = driver.findElement(By.cssSelector("form[action='/category'] button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("document.querySelector('#name').removeAttribute('required');");
        nameField.clear();
        nameField.sendKeys(name);
        colorField.clear();
        colorField.sendKeys(color);
        addButton.click();
    }

    public String getErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".error-message")
        ));
        return errorDiv.getText();
    }

    public boolean isCategoryListed(String categoryName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[@class='table table-striped']//tbody//tr//td[2][normalize-space(text())='" + categoryName + "']")
            ));

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

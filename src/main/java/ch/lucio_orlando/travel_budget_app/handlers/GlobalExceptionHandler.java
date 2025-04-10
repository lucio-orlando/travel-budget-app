package ch.lucio_orlando.travel_budget_app.handlers;

import ch.lucio_orlando.travel_budget_app.exceptions.InvalidDataException;
import ch.lucio_orlando.travel_budget_app.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.error.show-exception}")
    private boolean showException;

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        List<String> stacktrace = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList();
        model.addAttribute("exception", ex);
        model.addAttribute("stacktrace", stacktrace);
        model.addAttribute("showException", showException);
        return "error/404";
    }

    @ExceptionHandler({InvalidDataException.class, Exception.class})
    public String handleInvalidData(InvalidDataException ex, Model model) {
        List<String> stacktrace = Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList();
        model.addAttribute("exception", ex);
        model.addAttribute("stacktrace", stacktrace);
        model.addAttribute("showException", showException);
        return "error/400";
    }
}

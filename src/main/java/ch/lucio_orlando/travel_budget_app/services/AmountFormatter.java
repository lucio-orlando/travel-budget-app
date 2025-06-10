package ch.lucio_orlando.travel_budget_app.services;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AmountFormatter {
    public static String formatAmount(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);

        return formatter.format(amount);
    }
}

package ch.lucio_orlando.travel_budget_app.services;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AmountFormatter {
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.GERMAN);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00", SYMBOLS);
    private static final DecimalFormat SHORT_FORMAT = new DecimalFormat("0.##", SYMBOLS);
    private static final String SUFFIX_BILLION = "B";
    private static final String SUFFIX_MILLION = "M";
    private static final String SUFFIX_THOUSAND = "K";

    static {
        SYMBOLS.setDecimalSeparator('.');
        SYMBOLS.setGroupingSeparator('\'');
    }

    public static String formatAmount(double amount) {
        return DECIMAL_FORMAT.format(amount);
    }

    public static String formatShort(double amount) {
        double abs = Math.abs(amount);
        String suffix;
        double value;

        if (abs >= 1_000_000_000) {
            value = amount / 1_000_000_000;
            suffix = SUFFIX_BILLION;
        } else if (abs >= 1_000_000) {
            value = amount / 1_000_000;
            suffix = SUFFIX_MILLION;
        } else if (abs >= 1_000) {
            value = amount / 1_000;
            suffix = SUFFIX_THOUSAND;
        } else {
            return SHORT_FORMAT.format(amount); // no suffix
        }

        return SHORT_FORMAT.format(value) + suffix;
    }
}

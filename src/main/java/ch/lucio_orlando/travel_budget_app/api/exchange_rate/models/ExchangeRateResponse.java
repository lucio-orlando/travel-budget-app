package ch.lucio_orlando.travel_budget_app.api.exchange_rate.models;

public record ExchangeRateResponse(
        String result,
        String baseCode,
        String targetCode,
        double conversion_result,
        double exchangeRate
) {

    public boolean isSuccessful() {
        return result.equals("success");
    }
}

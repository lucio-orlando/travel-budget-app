package ch.lucio_orlando.travel_budget_app.models.api_response;

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

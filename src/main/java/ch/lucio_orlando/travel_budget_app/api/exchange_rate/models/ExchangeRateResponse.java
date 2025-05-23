package ch.lucio_orlando.travel_budget_app.api.exchange_rate.models;

public record ExchangeRateResponse(
        String result,
        String base_code,
        String target_code,
        double conversion_result,
        double conversion_rate
) {

    public boolean isSuccessful() {
        return result.equals("success");
    }
}

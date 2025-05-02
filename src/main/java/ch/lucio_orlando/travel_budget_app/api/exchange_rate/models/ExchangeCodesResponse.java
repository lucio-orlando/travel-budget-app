package ch.lucio_orlando.travel_budget_app.api.exchange_rate.models;

import ch.lucio_orlando.travel_budget_app.api.exchange_rate.deserializer.SupportedCodeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record ExchangeCodesResponse(String result, List<SupportedCode> supported_codes) {

    @JsonDeserialize(using = SupportedCodeDeserializer.class)
    public record SupportedCode(String code, String name) {}

    public boolean isSuccessful() {
        return result.equals("success");
    }
}

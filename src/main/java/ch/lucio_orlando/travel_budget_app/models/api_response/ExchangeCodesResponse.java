package ch.lucio_orlando.travel_budget_app.models.api_response;

import ch.lucio_orlando.travel_budget_app.deserializer.SupportedCodeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record ExchangeCodesResponse(String result, List<SupportedCode> supported_codes) {

    @JsonDeserialize(using = SupportedCodeDeserializer.class)
    public record SupportedCode(String code, String name) {}

    public boolean isSuccessful() {
        return result.equals("success");
    }
}

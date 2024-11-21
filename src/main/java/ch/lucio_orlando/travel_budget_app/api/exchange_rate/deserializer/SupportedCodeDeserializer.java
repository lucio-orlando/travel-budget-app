package ch.lucio_orlando.travel_budget_app.api.exchange_rate.deserializer;

import ch.lucio_orlando.travel_budget_app.api.exchange_rate.models.ExchangeCodesResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class SupportedCodeDeserializer extends JsonDeserializer<ExchangeCodesResponse.SupportedCode> {

    @Override
    public ExchangeCodesResponse.SupportedCode deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String[] array = parser.readValueAs(String[].class);
        return new ExchangeCodesResponse.SupportedCode(array[0], array[1]);
    }
}

package ch.lucio_orlando.travel_budget_app.services;

import ch.lucio_orlando.travel_budget_app.models.Currency;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ExchangeRateApiService {
    private final CurrencyService currencyService;
    private final WebClient webClient;

    @Value("${app.allow-api-calls}")
    private boolean allowApiCalls;

    @Value("${app.exchange-rate-api-key}")
    private String apiKey;

    @Value("${app.exchange-rate-api-url}")
    private String apiUrl;

    public ExchangeRateApiService(CurrencyService currencyService) {
        this.currencyService = currencyService;
        this.webClient = WebClient
                .builder()
                .baseUrl(apiUrl + "/" + apiKey + "/")
                .build();
    }

    public double getExchangeAmount(Currency base, Currency target, double amount) {
        if (!allowApiCalls) return amount;

        Map body = webClient.get().uri("pair/{base}/{target}/{amount}", base.getCode(), target.getCode(), amount)
                .retrieve()
                .bodyToMono(Map.class).block();

        if (body != null) {
            return Double.parseDouble(Objects.requireNonNull(body.get("conversion_result")).toString());
        }

        throw new RuntimeException("Failed to get exchange rate");
    }

    @PostConstruct
    public void loadSupportedCurrencies() {
        if (!allowApiCalls) return;

        Map body = webClient.get().uri("codes")
                .retrieve()
                .bodyToMono(Map.class).block();

        if (body != null) {
            List<List<String>> currencies = (List<List<String>>) body.get("supported_codes");

            currencies.forEach(code -> currencyService.saveCurrency(new Currency(code.get(0), code.get(1))));
        }
    }
}

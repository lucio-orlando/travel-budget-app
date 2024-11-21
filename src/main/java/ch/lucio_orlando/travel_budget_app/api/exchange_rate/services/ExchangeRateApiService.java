package ch.lucio_orlando.travel_budget_app.api.exchange_rate.services;

import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.api.exchange_rate.models.ExchangeCodesResponse;
import ch.lucio_orlando.travel_budget_app.api.exchange_rate.models.ExchangeRateResponse;
import ch.lucio_orlando.travel_budget_app.services.CurrencyService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ExchangeRateApiService {
    private final CurrencyService currencyService;
    private final WebClient webClient;

    @Value("${app.allow-api-calls}")
    private boolean allowApiCalls;

    public ExchangeRateApiService(
            CurrencyService currencyService,
            @Value("${app.exchange-rate-api-key}") String apiKey,
            @Value("${app.exchange-rate-api-url}") String apiUrl
    ) {
        this.currencyService = currencyService;
        this.webClient = WebClient
                .builder()
                .baseUrl(apiUrl + "/" + apiKey + "/")
                .build();
    }

    public double getExchangeAmount(Currency base, Currency target, double amount) {
        if (!allowApiCalls) return amount;

        ExchangeRateResponse response = webClient.get().uri("pair/{base}/{target}/{amount}", base.getCode(), target.getCode(), amount)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class).block();

        if (response != null && response.isSuccessful()) {
            if (response.conversion_result() != 0) {
                return response.conversion_result();
            } else if (response.exchangeRate() != 0) {
                return response.exchangeRate() * amount;
            }
        }

        throw new RuntimeException("Failed to get exchange rate");
    }

    @PostConstruct
    public void loadSupportedCurrencies() {
        if (!allowApiCalls) return;

        ExchangeCodesResponse response = webClient.get().uri("codes")
                .retrieve()
                .bodyToMono(ExchangeCodesResponse.class).block();

        if (response != null && response.isSuccessful()) {
            response.supported_codes().forEach(
                    supportedCode -> currencyService.saveCurrency(
                            new Currency(supportedCode.code(), supportedCode.name())
                    )
            );
        } else {
            throw new RuntimeException("Failed to load supported currencies");
        }
    }
}

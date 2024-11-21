package ch.lucio_orlando.travel_budget_app.api.unsplash.services;

import ch.lucio_orlando.travel_budget_app.api.unsplash.models.UnsplashResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UnsplashApiService {
    private final WebClient webClient;

    public UnsplashApiService(@Value("${app.unsplash-api-key}") String apiKey, @Value("${app.unsplash-api-url}") String apiUrl) {
        this.webClient = WebClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Client-ID " + apiKey)
                .build();
    }

    public String getPhotoUrl(String query) {
        UnsplashResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/photos/random")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(UnsplashResponse.class).block();
        return response != null ? response.urls().regular() : null;
    }
}

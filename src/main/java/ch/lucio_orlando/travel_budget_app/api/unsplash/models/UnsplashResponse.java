package ch.lucio_orlando.travel_budget_app.api.unsplash.models;

public record UnsplashResponse(Urls urls) {
    public record Urls(
            String raw,
            String full,
            String regular,
            String small,
            String thumb
    ) {}
}

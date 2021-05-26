package dev.danielpadua.micronautexampleapi;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("app")
public class ApplicationConfig {
    private String apiKey;
    private String baseUri;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}

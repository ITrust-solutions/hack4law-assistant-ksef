package pl.hack4law.assistant;

import io.alapierre.ksef.client.ApiClient;
import io.alapierre.ksef.client.JsonSerializer;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneFakturaApi;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneSesjaApi;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneZapytaniaApi;
import io.alapierre.ksef.client.okhttp.OkHttpApiClient;
import io.alapierre.ksef.client.serializer.gson.GsonJsonSerializer;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
@Factory
@Slf4j
public class BeanFactory {

    @Singleton
    public JsonSerializer jsonSerializer() {
        log.info("creating JsonSerializer");
        return new GsonJsonSerializer();
    }

    @Singleton
    public ApiClient apiClient(JsonSerializer serializer, KsefConfig config) {
        log.info("creating apiClient for {} environment", config.getEnvironment());
        return new OkHttpApiClient(serializer, config.getEnvironment());
    }

    @Singleton
    public InterfejsyInteraktywneSesjaApi sesjaApi(ApiClient apiClient) {
        log.info("creating InterfejsyInteraktywneSesjaApi");
        return new InterfejsyInteraktywneSesjaApi(apiClient);
    }

    @Singleton
    public InterfejsyInteraktywneFakturaApi fakturaApi(ApiClient apiClient) {
        log.info("creating InterfejsyInteraktywneFakturaApi");
        return new InterfejsyInteraktywneFakturaApi(apiClient);
    }

    @Singleton
    public InterfejsyInteraktywneZapytaniaApi zapytaniaApi(ApiClient apiClient) {
        log.info("creating InterfejsyInteraktywneZapytaniaApi");
        return new InterfejsyInteraktywneZapytaniaApi(apiClient);
    }

}

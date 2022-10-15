package pl.hack4law.assistant;

import io.alapierre.ksef.client.AbstractApiClient;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
@ConfigurationProperties("ksef")
@Context
@Getter
@Setter
public class KsefConfig {

    @NotEmpty
    private String token;

    @NotNull
    private AbstractApiClient.Environment environment;

    private String nip;
}

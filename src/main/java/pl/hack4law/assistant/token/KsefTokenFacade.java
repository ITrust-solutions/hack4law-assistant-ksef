package pl.hack4law.assistant.token;

import io.alapierre.ksef.client.ApiException;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneSesjaApi;
import io.alapierre.ksef.client.model.rest.auth.AuthorisationChallengeRequest;
import io.alapierre.ksef.client.model.rest.auth.AuthorisationChallengeResponse;
import io.alapierre.ksef.client.model.rest.auth.InitSignedResponse;
import io.alapierre.ksef.xml.model.AuthRequestUtil;
import io.alapierre.ksef.xml.model.AuthTokenRequestSerializer;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest;
import pl.hack4law.assistant.KsefConfig;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
@Slf4j
@RequiredArgsConstructor
@Singleton
public class KsefTokenFacade {

    private final InterfejsyInteraktywneSesjaApi api;
    private final KsefConfig config;

    public @NotNull InitSignedResponse authByToken(@NotNull String identifier, @NotNull String env) throws ApiException, ParseException {
        AuthorisationChallengeResponse challengeResponse = this.api.authorisationChallengeCall(identifier, AuthorisationChallengeRequest.IdentifierType.onip);
        log.debug("challengeResponse = {}", challengeResponse);
        Date timestamp = PublicKeyEncoder.parseChallengeTimestamp(challengeResponse.getTimestamp());
        PublicKeyEncoder encoder = PublicKeyEncoder.withBundledKey(env);
        String encryptedToken = encoder.encodeSessionToken(config.getToken(), timestamp);
        InitSessionTokenRequest request = AuthRequestUtil.prepareTokenAuthRequest(challengeResponse.getChallenge(), identifier, encryptedToken);
        log.debug("Token request {}", request);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        AuthTokenRequestSerializer serializer = new AuthTokenRequestSerializer();
        serializer.toStream(request, os);
        return this.api.initSessionTokenCall(os.toByteArray());
    }

}

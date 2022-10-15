package pl.hack4law.assistant;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.01.06
 */
@Singleton
@Slf4j
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    @Value("${auth.user}")
    private String user;

    @Value("${auth.password}")
    private String password;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            if ( authenticationRequest.getIdentity().equals(user) && authenticationRequest.getSecret().equals(password) ) {
                log.debug("Login successfully {}", user);
                val roles = List.of("ROLE_ADMIN");
                emitter.next(AuthenticationResponse.success((String) authenticationRequest.getIdentity(), roles));
                emitter.complete();
            } else {
                log.warn("Login failed {}", authenticationRequest.getIdentity());
                emitter.error(AuthenticationResponse.exception());
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }

}

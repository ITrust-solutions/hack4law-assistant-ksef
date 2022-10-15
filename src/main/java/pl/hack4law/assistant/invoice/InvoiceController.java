package pl.hack4law.assistant.invoice;

import io.alapierre.ksef.client.ApiException;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneFakturaApi;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneZapytaniaApi;
import io.alapierre.ksef.client.model.rest.query.InvoiceQueryRequest;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import pl.hack4law.assistant.KsefConfig;
import pl.hack4law.assistant.token.KsefTokenFacade;

import javax.annotation.PreDestroy;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
@Controller("/invoice")
@RequiredArgsConstructor
@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
public class InvoiceController {

    private final KsefConfig config;
    private final InterfejsyInteraktywneFakturaApi fakturaApi;
    private final KsefTokenFacade tokenFacade;

    private byte[] tokenBuffer;
    private LocalDateTime tokenLastUsageDate;

    private final InterfejsyInteraktywneZapytaniaApi zapytaniaApi;

    @Get(uri = "/{invoiceId}")
    public HttpResponse<byte[]> loadInvoiceByKsefNumber(@PathVariable String invoiceId) throws IOException, ParseException {

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            val sessionToken = loginToKsef();
            fakturaApi.getInvoice(invoiceId, sessionToken, os);
            return HttpResponse.ok(os.toByteArray());
        } catch (ApiException ex) {
            log.error("Error contacting KSef {}", ex.getResponseBody());
            throw new RemoteException("Cant get invoice");
        }
    }

    private String loginToKsef() throws ApiException, ParseException {

        try {

            if (tokenBuffer == null) {
                log.debug("get new token");
                val sessionToken = tokenFacade.authByToken(config.getNip(), config.getEnvironment().name());
                tokenBuffer = sessionToken.getSessionToken().getToken().getBytes();
                tokenLastUsageDate = LocalDateTime.now();
            } else {
                log.debug("using existing token");
            }

            return new String(tokenBuffer);
        } catch (ApiException e) {
            log.error("Error contacting KSef {}", e.getResponseBody());
            throw e;
        }
    }

    @Get
    public InvoiceQueryResponseDTO loadInvoices(
            @Parameter @Format("yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
            @Parameter @Format("yyyy-MM-dd'T'HH:mm:ss") LocalDateTime to,
            @Parameter @Min(1) @Max(100) int pageSize,
            @Parameter @Min(0) int pageOffset) throws ApiException, ParseException {


        val sessionToken = tokenFacade.authByToken(config.getNip(), config.getEnvironment().name());

        val dateFrom = zapytaniaApi.convertDate(from);
        val dateTo = zapytaniaApi.convertDate(to);

        val resp = zapytaniaApi.invoiceQuery(
                sessionToken.getSessionToken().getToken(),
                InvoiceQueryRequest.builder()
                        .queryCriteria(InvoiceQueryRequest.QueryCriteria.builder()
                                .type("incremental")
                                .subjectType(config.getNip())
                                .acquisitionTimestampThresholdFrom(dateFrom)
                                .acquisitionTimestampThresholdTo(dateTo)
                                .build())
                        .build(),
                pageSize,
                pageOffset);

        return new InvoiceQueryResponseMapper().toInvoiceQueryResponseDTO(resp);
    }

    @PreDestroy
    void shutdown() {
        log.debug("Destroying app context");
        tokenBuffer = new byte[0];
    }

}

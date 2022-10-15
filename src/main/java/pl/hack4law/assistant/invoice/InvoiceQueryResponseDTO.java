package pl.hack4law.assistant.invoice;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
@Value
@Builder
@Introspected
public class InvoiceQueryResponseDTO {

    int count;
    int page;

    List<String> invoices;

}

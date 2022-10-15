package pl.hack4law.assistant.invoice;

import io.alapierre.ksef.client.model.rest.query.InvoiceQueryResponse;

import java.util.stream.Collectors;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2022.10.15
 */
public class InvoiceQueryResponseMapper {

    public InvoiceQueryResponseDTO toInvoiceQueryResponseDTO(InvoiceQueryResponse response) {
        return InvoiceQueryResponseDTO.builder()
                .count(response.getNumberOfElements())
                .page(response.getPageOffset())
                .invoices(response.getInvoiceHeaderList().stream()
                        .map(InvoiceQueryResponse.InvoiceHeaderList::getKsefReferenceNumber)
                        .collect(Collectors.toList()))
                .build();
    }

}

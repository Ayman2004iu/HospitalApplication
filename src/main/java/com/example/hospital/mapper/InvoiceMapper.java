package com.example.hospital.mapper;

import com.example.hospital.dto.response.InvoiceResponse;
import com.example.hospital.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InvoiceItemMapper.class})
public interface InvoiceMapper {

    @Mappings({
            @Mapping(source = "visit.id", target = "visitId"),
            @Mapping(source = "patient.id", target = "patientId")
    })
    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> invoices);
}
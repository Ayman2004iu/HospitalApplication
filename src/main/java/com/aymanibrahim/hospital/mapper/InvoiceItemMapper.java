package com.aymanibrahim.hospital.mapper;

import com.aymanibrahim.hospital.dto.response.InvoiceItemResponse;
import com.aymanibrahim.hospital.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {

    @Mapping(source = "invoice.id", target = "invoiceId")
    InvoiceItemResponse toResponse(InvoiceItem item);

}

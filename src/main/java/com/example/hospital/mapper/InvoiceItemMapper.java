package com.example.hospital.mapper;

import com.example.hospital.dto.response.InvoiceItemResponse;
import com.example.hospital.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {

    @Mapping(source = "invoice.id", target = "invoiceId")
    InvoiceItemResponse toResponse(InvoiceItem item);

}

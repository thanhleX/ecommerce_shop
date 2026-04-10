package com.example.shop.application.mapper;

import com.example.shop.application.dto.request.AddressRequest;
import com.example.shop.application.dto.response.AddressResponse;
import com.example.shop.domain.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    Address toAddress(AddressRequest request);

    AddressResponse toAddressResponse(Address address);

    List<AddressResponse> toAddressResponseList(List<Address> addresses);

    void updateAddress(@MappingTarget Address address, AddressRequest request);
}

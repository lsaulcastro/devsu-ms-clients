package com.devsu.clients.mapper;

import com.devsu.clients.domain.model.Customer;
import com.devsu.clients.dto.CustomerResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
}
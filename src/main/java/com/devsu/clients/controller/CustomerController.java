package com.devsu.clients.controller;

import com.devsu.clients.dto.ChangePasswordRequest;
import com.devsu.clients.dto.CreateCustomerRequest;
import com.devsu.clients.dto.CustomerResponse;
import com.devsu.clients.dto.UpdateCustomerRequest;
import com.devsu.clients.dto.common.ApiResponse;
import com.devsu.clients.mapper.CustomerMapper;
import com.devsu.clients.service.CustomerCommandService;
import com.devsu.clients.service.CustomerQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerCommandService commandService;
    private final CustomerQueryService queryService;
    private final CustomerMapper customerMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerMapper.toResponse(commandService.create(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{customerId}")
                .buildAndExpand(response.customerId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), location.getPath(), response));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> findByCustomerId(@PathVariable String customerId) {
        CustomerResponse response = customerMapper.toResponse(queryService.findByCustomerId(customerId));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "/customers/" + customerId, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> findAll() {
        List<CustomerResponse> response = customerMapper.toResponseList(queryService.findAll());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "/customers", response));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable String customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerMapper.toResponse(commandService.update(customerId, request));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "/customers/" + customerId, response));
    }

    @PatchMapping("/{customerId}/password")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @PathVariable String customerId,
            @Valid @RequestBody ChangePasswordRequest request) {
        commandService.changePassword(customerId, request);
    }

    @DeleteMapping("/{customerId}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String customerId) {
        commandService.deactivate(customerId);
    }
}
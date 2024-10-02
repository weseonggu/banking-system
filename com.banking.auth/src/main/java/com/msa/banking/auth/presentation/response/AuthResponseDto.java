package com.msa.banking.auth.presentation.response;

import com.msa.banking.auth.domain.model.Customer;
import com.msa.banking.auth.domain.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto implements Serializable {

    private UUID id;

    private String username;

    private String name;
    private String email;
    private String phoneNumber;

    private String city;
    private String street;
    private String zipcode;

    private String role;

    public static AuthResponseDto toDto(Customer customer) {
        return new AuthResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress().getCity(),
                customer.getAddress().getStreet(),
                customer.getAddress().getZipcode(),
                customer.getRole().name()
        );
    }

    public static AuthResponseDto toDto(Employee employee) {
        return new AuthResponseDto(
                employee.getId(),
                employee.getUsername(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getAddress().getCity(),
                employee.getAddress().getStreet(),
                employee.getAddress().getZipcode(),
                employee.getRole().name()
        );
    }
}

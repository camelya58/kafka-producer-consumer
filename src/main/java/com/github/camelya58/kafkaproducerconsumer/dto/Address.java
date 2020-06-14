package com.github.camelya58.kafkaproducerconsumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class UserDTO is simple POJO-object
 *
 * @author Kamila Meshcheryakova
 */
@Data
@AllArgsConstructor
public class Address {

    private String country;
    private String city;
    private String street;
    private Long homeNumber;
    private Long flatNumber;
}

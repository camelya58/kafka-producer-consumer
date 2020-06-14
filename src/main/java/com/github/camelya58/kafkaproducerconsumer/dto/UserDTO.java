package com.github.camelya58.kafkaproducerconsumer.dto;

import lombok.Data;

/**
 * Class UserDTO is simple POJO-object and contains data of user
 *
 * @author Kamila Meshcheryakova
 */
@Data
public class UserDTO {

    private Long age;
    private String name;
    private Address address;
}

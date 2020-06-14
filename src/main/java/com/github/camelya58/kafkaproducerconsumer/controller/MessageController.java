package com.github.camelya58.kafkaproducerconsumer.controller;

import com.github.camelya58.kafkaproducerconsumer.dto.Address;
import com.github.camelya58.kafkaproducerconsumer.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class MessageController represents simple REST-Controller with a single method.
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequestMapping("msg")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<Long, UserDTO> kafkaTemplate;

    @PostMapping
    public void send(Long msgId, UserDTO msg) {
        msg.setAddress(new Address("Russia", "Moscow", "Lenina", 2L, 100L));
        ListenableFuture<SendResult<Long, UserDTO>> future = kafkaTemplate.send("msg", msgId, msg);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
}

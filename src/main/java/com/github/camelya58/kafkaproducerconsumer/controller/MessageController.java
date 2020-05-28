package com.github.camelya58.kafkaproducerconsumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class MessageController represents simple REST-Controller with a single method.
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    public void send(String msgId, String msg) {
        kafkaTemplate.send("New_topic", msgId, msg);
    }
}

package com.github.camelya58.kafkaproducerconsumer;

import com.github.camelya58.kafkaproducerconsumer.dto.UserDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@SpringBootApplication
public class KafkaProducerConsumerApplication {

    @KafkaListener(topics="msg")
    public void orderListener(ConsumerRecord<Long, UserDTO> record) {
        System.out.println(record.partition());
        System.out.println(record.key());
        System.out.println(record.value());
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerConsumerApplication.class, args);
    }

}

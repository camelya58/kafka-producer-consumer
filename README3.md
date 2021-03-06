## Step 13
Complicate the consumer.
 
Change the consumer method with a string in the parameter to another type as producer has
and get some information about received message.

```java
@EnableKafka
@SpringBootApplication
public class KafkaProducerConsumerApplication {
    
     @KafkaListener(topics="msg")
     public void orderListener(ConsumerRecord<Long, UserDTO> record) {
         System.out.println(record.partition());
         System.out.println(record.key());
         System.out.println(record.value());
     }
}
```

You'll get the following:

```
0
        
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
```

There isn't any information about key.

Due to the fact that the default key type is "string", but we want to get "long"
we need to configure some settings for a consumer.

## Step 14
Create class KafkaConsumerConfig.

It has a similar configurations as a producer but we use a deserializer instead of a serializer.

```java
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        return props;
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, UserDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, UserDTO> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
}
```

We will receive all parameters.

```
0
4       
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
```

Now everything works correct.

This is an easy way to get to know Apache Kafka.

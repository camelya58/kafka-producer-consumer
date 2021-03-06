## Step 11.
To set Long as the key type and DTO as the value type, you need to manually create the KafkaTemplate object.

The object of ProducerFactory<K, V> interface is passed to the constructor as a parameter.
For example, DefaultKafkaProducerFactory<> with producer's config as a parameter.
Therefore, the next step is to create KafkaProducerConfig and configure the producer’s parameters.
11.1. Create a UserDTO class.
```java
@Data
public class UserDTO {

    private long age;
    private String name;
    private Address address;
}

@Data
@AllArgsConstructor
public class Address {

    private String country;
    private String city;
    private String street;
    private long homeNumber;
    private long flatNumber;
}
```
 11.2. Create a KafkaProducerConfig class.
```java
@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<Long, UserDTO> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, UserDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```
11.3. Fix changes in a MesssageController class.
```java
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
```
## Step 12.
Make a request in Postman.

![Image alt](https://github.com/camelya58/kafka-producer-consumer/blob/sophistication/image2.png)

And we get the following string in console.
```
SendResult [producerRecord=ProducerRecord(topic=msg, partition=null, headers=RecordHeaders(headers = 
[RecordHeader(key = __TypeId__, value = [99, 111, 109, 46, 103, 105, 116, 104, 117, 98, 46, 99, 97, 109, 101, 108, 121,
97, 53, 56, 46, 107, 97, 102, 107, 97, 112, 114, 111, 100, 117, 99, 101, 114, 99, 111, 110, 115, 117, 109, 101, 114, 
46, 100, 116, 111, 46, 85, 115, 101, 114, 68, 84, 79])], isReadOnly = true), key=9, value=UserDTO(age=18, name=Igor, 
address=Address(country=Russia, city=Moscow, street=Lenina, homeNumber=2, flatNumber=100)), timestamp=null), 
recordMetadata=msg-0@27]
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
```

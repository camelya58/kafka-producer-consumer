# kafka-producer-consumer
Simple project with Apache Kafka to demonstrate message exchange.

Stack: Apache Kafka, Maven, Lombok.

## Step 1
Install Apache Kafka for MacOS X.
```
brew install kafka
```
You can skip steps 2-4 as unnecessary.
## Step 2.
Create directories to save the data.
```
cd kafka_2.13-2.5.0
mkdir data
mkdir data/zookeeper
mkdir data/kafka
```

## Step 3.
Configure Zookeeper.
- find the folder "config";
- open the file "zookeeper.properties";
- find and change the path for dataDir to the desired directory.
Change this:
```properties
dataDir=/tmp/zookeeper
```
to this, for example:
```properties
dataDir=/Users/user/Applications/kafka_2.13-2.5.0/data/zookeeper
```

## Step 4.
Configure Kafka server.
- find the folder "config";
- open the file "server.properties";
- find and change the path for log.dirs to the desired directory.
Change this:
```properties
log.dirs=/tmp/kafka-logs
```
to this, for example:
```properties
log.dirs=/Users/igor/Applications/kafka_2.13-2.5.0/data/kafka
```

## Step 5.
Start zookeeper and kafka in terminal. Set up path to zookeeper.properties and server.properties.
```
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties & kafka-server-start /usr/local/etc/kafka/server.properties
```

## Step 6.
Open IntelliJ IDEA and create spring project.

Add dependencies: org.springframework.kafka, spring-boot-starter-web and Lombok.
Or you can you [Spring initializr](https://start.spring.io/).
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
</dependencies>
```

## Step 7.
To send a message we need to create an object - KafkaTemplate<K, V> (the default key and value type is string). 

This object will be created in a controller class. 

We need to call the send method from this object with parameters - send(String topic, K key, V value).
If the topic named in the method doesn't exist yet, it will be created automatically.
The producer is ready.

Our controller maps to localhost:8080/message.
```java
@RestController
@RequestMapping("msg")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    public void send(String msgId, String msg) {
        kafkaTemplate.send("msg", msgId, msg);
    }
}
```
## Step 8.
To create a consumer we need to create method with annotation @KafkaListener with a topic name in the parameters.
The method has a single parameter - a message with a type passed by the producer. 
We also need to mark the class in which the consumer is created with the annotation @EnableKafka.
```java
@EnableKafka
@SpringBootApplication
public class KafkaProducerConsumerApplication {

    @KafkaListener(topics="msg")
    public void messageListener(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerConsumerApplication.class, args);
    }

}
```
It is necessary to write the group-id for the consumer in application.properties. Otherwise, the application will not start.
```properties
spring.kafka.consumer.group-id=app.2
```

## Step 9
The simple project is ready. Now We need to send a request with a key and value of type String using Postman.

![Image alt](https://github.com/camelya58/kafka-producer-consumer/blob/sophistication/image1.png)

If we received this message in the console, everything works correctly!
```
Hello, World!
```
This is a very simple configuration.

A real project using Kafka is more complicated. 

## Step 10.
To get the result of posting messages you need to add ListenableFuture<SendResult<K, V>> 
and call the addCallback method with parameters - SuccessCallback and FailureCallback.

These are functional interfaces. The method of the first interface will be called in case of successful sending of the message, 
and the method of the second interface in case of failure.
```java
public void send(String msgId, String msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("New_topic", msgId, msg);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
```
We will receive:
```
SendResult [producerRecord=ProducerRecord(topic=New_topic, partition=null, headers=RecordHeaders(headers = [], 
isReadOnly = true), key=1, value=Hello, World!, timestamp=null), recordMetadata=New_topic-0@2]
```

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
SendResult [producerRecord=ProducerRecord(topic=msg, partition=null, headers=RecordHeaders(headers = [RecordHeader(key = __TypeId__, value = [99, 111, 109, 46, 103, 105, 116, 104, 117, 98, 46, 99, 97, 109, 101, 108, 121, 97, 53, 56, 46, 107, 97, 102, 107, 97, 112, 114, 111, 100, 117, 99, 101, 114, 99, 111, 110, 115, 117, 109, 101, 114, 46, 100, 116, 111, 46, 85, 115, 101, 114, 68, 84, 79])], isReadOnly = true), key=9, value=UserDTO(age=18, name=Igor, address=Address(country=Russia, city=Moscow, street=Lenina, homeNumber=2, flatNumber=100)), timestamp=null), recordMetadata=msg-0@27]
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
```

## Step 13
Complicate the consumer.

Change the consumer method with a string in the parameter to another type as producer has
and get some information about received message.
```java
@KafkaListener(topics="msg")
    public void orderListener(ConsumerRecord<Long, UserDTO> record) {
        System.out.println(record.partition());
        System.out.println(record.key());
        System.out.println(record.value());
    }
```
And you'll get the following:
```
0
       
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
SendResult [producerRecord=ProducerRecord(topic=msg, partition=null, 
headers=RecordHeaders(headers = [RecordHeader(key = __TypeId__, value = 
[99, 111, 109, 46, 103, 105, 116, 104, 117, 98, 46, 99, 97, 109, 101, 108, 121, 97, 53, 56, 46, 107, 97, 102, 107, 97, 
112, 114, 111, 100, 117, 99, 101, 114, 99, 111, 110, 115, 117, 109, 101, 114, 46, 100, 116, 111, 46, 85, 115, 101, 114, 
68, 84, 79])], isReadOnly = true), key=3, value=UserDTO(age=18, name=Igor, address=Address(country=Russia, city=Moscow,
street=Lenina, homeNumber=2, flatNumber=100)), timestamp=null), recordMetadata=msg-0@28]
```
There isn't any information about key.
Due to the fact that the default key type is "string", but we want to get "long"
we need to configure some settings for a consumer.
 
 ## Step 14
Create class KafkaConsumerConfig.
It has the same configurations as a producer but we use a deserializer instead of a serializer.
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
And now we will receive all parameters.
```
0
4       
{"age":18,"name":"Igor","address":{"country":"Russia","city":"Moscow","street":"Lenina","homeNumber":2,"flatNumber":100}}
SendResult [producerRecord=ProducerRecord(topic=msg, partition=null, 
headers=RecordHeaders(headers = [RecordHeader(key = __TypeId__, value = 
[99, 111, 109, 46, 103, 105, 116, 104, 117, 98, 46, 99, 97, 109, 101, 108, 121, 97, 53, 56, 46, 107, 97, 102, 107, 97, 
112, 114, 111, 100, 117, 99, 101, 114, 99, 111, 110, 115, 117, 109, 101, 114, 46, 100, 116, 111, 46, 85, 115, 101, 114, 
68, 84, 79])], isReadOnly = true), key=3, value=UserDTO(age=18, name=Igor, address=Address(country=Russia, city=Moscow,
street=Lenina, homeNumber=2, flatNumber=100)), timestamp=null), recordMetadata=msg-0@28]
```
Now everything works correct.

This is an easy way to get to know Apache Kafka.
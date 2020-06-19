# kafka-producer-consumer
Simple project with Apache Kafka to demonstrate message exchange.

Stack: Apache Kafka, Maven, Lombok.

## Step 1
Install [Apache Kafka](https://kafka.apache.org/downloads) for MacOS X.
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
```
dataDir=/tmp/zookeeper
```
to this, for example:
```
dataDir=/Users/user/Applications/kafka_2.13-2.5.0/data/zookeeper
```

## Step 4.
Configure Kafka server.
- find the folder "config";
- open the file "server.properties";
- find and change the path for log.dirs to the desired directory.
Change this:
```
log.dirs=/tmp/kafka-logs
```
to this, for example:
```
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
```
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
@RestController
@RequestMapping("msg")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaTemplate<Long, UserDTO> kafkaTemplate;

    @PostMapping
    public void send(String msgId, String msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("New_topic", msgId, msg);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
}
```
We will receive:
```
SendResult [producerRecord=ProducerRecord(topic=New_topic, partition=null, headers=RecordHeaders(headers = [], 
isReadOnly = true), key=1, value=Hello, World!, timestamp=null), recordMetadata=New_topic-0@2]
```

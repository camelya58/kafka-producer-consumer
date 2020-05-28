# kafka-producer-consumer
Simple project with Apache Kafka to demonstrate message exchange.

Stack: Apache Kafka, Maven, Lombok

## Step 1
Install Apache Kafka for MacOS X.
```
brew install kafka
```
## Step 2.
Create directories to save the data.
```
cd kafka_2.13-2.5.0
mkdir data
mkdir data/zookeeper
mkdir data/kafka
```

## Step 3.
Confugure Zookeeper.
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
Confugure Kafka server.
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
To send a message we need to create an object - KafkaTemplate<K, V>. 
This object will be created in a controller class. 

We need to call the send method from this object with parameters - send(String topic, K key, V value).
If the topic named in the method doesn't exist yet, it will be created automatically.
The producer is ready.

Our controller maps to localhost:8080/message.
```java
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
```
## Step 8.
To create a consumer we need to create method with annotation @KafkaListener with a topic name in the parameters.
The method has a single parameter - a message with a type passed by the producer. 
We also need to mark the class in which the consumer is created with the annotation @EnableKafka.
```java
@EnableKafka
@SpringBootApplication
public class KafkaProducerConsumerApplication {

    @KafkaListener(topics="New_topic")
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
spring.kafka.consumer.group-id=app.1
```

## Step 9
The simple project is ready. Now We need to send a request with a key and value of type String using Postman.

```json
{
  "msgId": "1",
  "msg": "Hello, World!"
}
```

If we received this message in the console, everything works correctly!
```
Hello, World!
```

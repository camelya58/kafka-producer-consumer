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

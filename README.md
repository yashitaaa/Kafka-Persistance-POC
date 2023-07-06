# Kafka-Persistance-POC

This application is a demonstration of Kafka's producer-consumer concept and how to use KSQL DB for stream processing and querying of data persisted in the Kafka Topic.

## **Overview**

The application is divided into several parts:

1. **Producer**: The producer service creates and sends messages to a specific Kafka topic.
2. **Kafka Stream**: A Kafka Stream is built over the topic where messages are persisted. These messages can be processed in real-time and used to perform various tasks like data filtering, transformations, aggregations, joins, windowing, and sessionization.
3. **KSQL DB**: KSQL DB is used to query the data persisted in the Kafka topic in a manner similar to traditional databases. It allows stream processing operations to be defined using an SQL-like syntax. This service in our application also allows the execution of these KSQL commands.
4. **Consumer**: The consumer service listens for and consumes the messages from the Kafka topic. After consumption, if required, it can insert the messages into specified tables.

All of the above steps can be performed using REST API calls via Postman.

## **How to Run**

1. Use the docker command **`docker-compose up`** to start all the services in teh correct order . This will then in turn start your Kafka server , zookeeper , ksqlDB and ksqlCLI on speficied ports.
2. Run the Spring Boot application.
3. Use Postman or a similar tool to hit the endpoint **`/kafka/topic`** with a POST request to create a topic by sending the topicName as a request parameter.
4. Use Postman or a similar tool to hit the endpoint **`/kafka/publish`** with a POST request to produce messages by sending the data and topicName as request body.
5. You can consume messages ans insert it into specific tables by making a GET request to **`/kafka/consume`** and sending in the topicName , consumerID and tableName in the request body.
6. KSQL queries can be performed with a POST request to **`/execute`**, sending a request body that includes the KSQL command and endpoint.
7. KSQL queries can also be performed through KSQL CLI by connectint to the ksqlDB using command **`docker exec -it ksqldb-cli ksql http://ksqldb-server:8088`** in teh terminal and can enter the interactive command line and perform basic SQL queries . 

## **Technologies Used**

- Spring Boot for application framework.
- Kafka for message passing.
- KSQL for stream processing.

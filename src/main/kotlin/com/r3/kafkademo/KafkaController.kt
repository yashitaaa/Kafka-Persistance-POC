package com.r3.kafkademo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpMethod

// KafkaController Class
// It exposes the REST endpoints for interacting with the Kafka topic.
@RestController
@RequestMapping("/kafka")
class KafkaController(
    private val producerService: ProducerService, // Service for producing messages
    private val consumerService : ConsumerService, // Service for consuming messages
    private val ksqlDBService: KsqlDBService, // Service for executing KSQL commands
    private val kafkaTopicService : KafkaTopicService // Service for managing Kafka topics
) {

    // POST endpoint for creating a Kafka topic
    @PostMapping("/topic")
    fun createTopic(@RequestParam topicName: String) {
        // Call the createTopic function from the KafkaTopicService to create a topic
        kafkaTopicService.createTopic(topicName, 1, 1)
    }

    // POST endpoint for publishing a message to the Kafka topic
    @PostMapping("/publish")
    fun publishMessage(@RequestBody  publish : PublishMessage) {
        // Call the send function from the ProducerService to publish a message to a Kafka topic
        producerService.send(publish.topic, publish.data)
    }

    // GET endpoint for consuming the message from the Kafka topic
    @GetMapping("/consume")
    fun consumeMessage(@RequestBody consume : ConsumeMessage): List<DataClass> {
        // Call the consumeMessages function from the ConsumerService to consume messages from a Kafka topic
        return consumerService.consumeMessages(consume.topic, consume.consumerID, consume.tableName)
    }

    // POST endpoint for executing a KSQL command
    @PostMapping("/execute")
    fun executePostKsqlCommand(@RequestBody ksqlCommand: KsqlCommand): ResponseEntity<String> {
        // Calling executeKsqlCommand function from the KsqlDBService to execute a KSQL command
        val result = ksqlDBService.executeKsqlCommand(ksqlCommand.endpoint, ksqlCommand.ksql, HttpMethod.POST)
        return ResponseEntity.ok(result)
    }


}

// Data class for holding information related to the publish message request
data class PublishMessage(
    val data : DataClass, // The data to be published
    val topic : String // The Kafka topic to which the data will be published
)

// Data class for holding information related to the consume message request
data class ConsumeMessage(
    val topic: String, // The Kafka topic from which to consume messages
    val tableName : String, // The KSQL table where the consumed messages will be inserted
    val consumerID: String // The ID of the consumer that will consume the messages
)

// Data class for holding information related to the KSQL command
data class KsqlCommand(
    val endpoint: String, // The endpoint to which the KSQL command will be sent
    val ksql: String // The KSQL command to be executed
)
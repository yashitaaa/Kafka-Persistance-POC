package com.r3.kafkademo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

// ProducerService Class
// It is a service responsible for producing the messages to a Kafka topic

@Service
class ProducerService(private val kafkaTemplate: KafkaTemplate<String, DataClass>) {
    // Logger for logging the events
    companion object {
        val log: Logger = LoggerFactory.getLogger(ProducerService::class.java)
    }

    // Function to send the message to the Kafka topic
    fun send(topic: String, data: DataClass) {
        log.info("Sending message to topic {}: {}", topic, data)
        // Send the data to the Kafka topic
        kafkaTemplate.send(topic, data).addCallback({
            log.info("Sent message=[{}] with offset=[{}]", data, it?.recordMetadata?.offset())
        }, {
            log.error("Unable to send message=[{}]", data, it)
        })
    }

}
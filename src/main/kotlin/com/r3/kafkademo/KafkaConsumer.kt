package com.r3.kafkademo

import com.r3.kafkademo.DataClass.Companion.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.stereotype.Service
import java.awt.image.ByteLookupTable
import java.time.Duration
import java.util.*
import kotlin.reflect.full.memberProperties

//ConsumerService Class
// It is a service responsible for consuming the messages from a Kafka topic and inserting the consumed messages into a KSQL table.

@Service
class ConsumerService @Autowired constructor(
    private val consumerFactory: ConsumerFactory<String, DataClass>,
    private val ksqlDBService: KsqlDBService) {


    companion object {
        val log: Logger = LoggerFactory.getLogger(ConsumerService::class.java)
    }

    // Function to consume the messages from the Kafka topic
    fun consumeMessages(topic: String, consumerId: String , table: String): List<DataClass> {
        log.info("consumeMessages called with topic : $topic and consumerID : $consumerId")

        // Creating a new Kafka consumer
        val consumer = consumerFactory.createConsumer(consumerId, "")
        consumer.use{
            // Subscribe to the given Kafka topic
            it.subscribe(listOf(topic))
            it.seekToBeginning(it.assignment())

            // Creating a mutable list for storing the consumed messages
            val messages = mutableListOf<DataClass>()
            // Polling the messages from the Kafka topic
            val records = it.poll(Duration.ofMillis(1000))
            // Iterating over the polled records
            for (record in records) {
                log.info("Received record : $record")
                messages.add(record.value())
                log.info("Consumed message from topic {}: {}", record.topic(), record.value())

                 //Insert the consumed message into the specific KSQL table if required
                 val jsonString = objectMapper.writeValueAsString(record.value()) // converting the json value into string before inserting in the table to match the schema of the table
                 val ksql = """INSERT INTO $table (KEY, VALUE) VALUES ('${record.value().key}', '$jsonString');"""

                ksqlDBService.executeKsqlCommand("ksql" ,ksql ,HttpMethod.POST)
            }
            val size = messages.size
            log.info("size :" ,size)
            return messages // return the consumed messages
        }
    }


}
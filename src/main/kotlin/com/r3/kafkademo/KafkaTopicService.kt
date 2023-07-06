package com.r3.kafkademo

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

//This service is responsible for creating new Kafka Topics

@Service
class KafkaTopicService {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    fun createTopic(topicName: String, numPartitions: Int, replicationFactor: Short) {
        //Creating a new Topic instance with the provided parameters
        val newTopic = NewTopic(topicName, numPartitions, replicationFactor)
        val client = AdminClient.create(mapOf(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers))
        client.createTopics(listOf(newTopic)).all().get()
    }


}
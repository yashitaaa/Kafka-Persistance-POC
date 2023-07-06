package com.r3.kafkademo

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.web.client.RestTemplate

@Configuration
class KafkaConfiguration {
    // Defining the servers to connect to
    private val bootstrapServers = "localhost:29092"

    // Creating a ProducerFactory bean which is responsible for creating Kafka Producer instances
    @Bean
    fun producerFactory(): ProducerFactory<String, DataClass> {
        // Defining producer-specific configuration properties
        val configProps = mapOf(
            // Setting the host and port the producer will use to establish an initial connection to the Kafka cluster
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            // Setting the serializer class for keys. This will convert keys to bytes.
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            // Setting the serializer class for values. This will convert values to bytes.
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to DataClass.DataClassSerializer::class.java
        )

        return DefaultKafkaProducerFactory(configProps)
    }

    // Creating a ConsumerFactory bean which is responsible for creating Kafka Consumer instances
    @Bean
    fun consumerFactory(): ConsumerFactory<String, DataClass> {
        // Define consumer-specific configuration properties
        val configProps = mapOf(
            // Setting the host and port the consumer will use to establish an initial connection to the Kafka cluster
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            // Setting the unique string that identifies the consumer group this consumer belongs to
            ConsumerConfig.GROUP_ID_CONFIG to "my-group",
            // Setting the deserializer class for keys
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            // Setting the deserializer class for values
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to DataClass.DataClassDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            // Setting what to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
        )

        return DefaultKafkaConsumerFactory(configProps)
    }

    // Create a ConcurrentKafkaListenerContainerFactory bean which is responsible for creating containers for methods annotated with @KafkaListener
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, DataClass> {
        // Creating a ConcurrentKafkaListenerContainerFactory
        val factory = ConcurrentKafkaListenerContainerFactory<String, DataClass>()
        // Setting the ConsumerFactory for the created factory
        factory.consumerFactory = consumerFactory()
        // Returning the configured ConcurrentKafkaListenerContainerFactory
        return factory
    }

    // Create a KafkaTemplate for sending messages to Kafka topics
    @Bean
    fun kafkaTemplate() : KafkaTemplate<String , DataClass> {
        // Returning a KafkaTemplate with the provided ProducerFactory
        return KafkaTemplate(producerFactory())
    }

    // Create a RestTemplate bean which is used to send HTTP requests.
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }


}
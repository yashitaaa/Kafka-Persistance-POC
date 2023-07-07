package com.r3.kafkademo

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
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
  // specifying the application properties to be used

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    private lateinit var auto_offset_reset : String

    @Value("\${spring.kafka.producer.key-serializer}")
    private lateinit var ProducerKeySerializer : String

    @Value("\${spring.kafka.consumer.key-deserializer}")
    private lateinit var ConsumerKeyDeserializer : String

    @Value("\${spring.kafka.producer.value-serializer}")
    private lateinit var ProducerValueSerializer : String

    @Value("\${spring.kafka.consumer.value-deserializer}")
    private lateinit var ConsumerValueDeserializer : String

    // Creating a ProducerFactory bean which is responsible for creating Kafka Producer instances
    @Bean
    fun producerFactory(): ProducerFactory<String, DataClass> {
        // Defining producer-specific configuration properties
        val configProps = mapOf(
            // Setting the host and port the producer will use to establish an initial connection to the Kafka cluster
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            // Setting the serializer class for keys. This will convert keys to bytes.
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to ProducerKeySerializer,
            // Setting the serializer class for values. This will convert values to bytes.
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ProducerValueSerializer
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
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            // Setting the deserializer class for keys
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to ConsumerKeyDeserializer,
            // Setting the deserializer class for values
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ConsumerValueDeserializer,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            // Setting what to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to auto_offset_reset
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
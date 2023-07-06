package com.r3.kafkademo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.protocol.types.Field.Str
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset


// Defining a DataClass to be used in your Kafka messages

data class transactions(val id : String , val status : String)

//@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class DataClass(val key: String, val value: transactions) {

    // Define a companion object to hold common properties across instances
    companion object {
        // Create a Jackson Object Mapper, used for converting objects to/from JSON
        val objectMapper = jacksonObjectMapper()
        // Create a logger instance for logging events in this class
        val log : Logger = LoggerFactory.getLogger(DataClass::class.java)
    }

    // Define a Serializer for DataClass. This will be used when producing messages to Kafka.
    class DataClassSerializer : Serializer<DataClass> {
        override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
        override fun close() {}

        // Method for converting a DataClass to a byte array for transmission
        override fun serialize(topic: String?, data: DataClass?): ByteArray {
            ByteArrayOutputStream().use { out ->
                // Convert the DataClass to JSON and write to the ByteArrayOutputStream
                objectMapper.writeValue(out, data)
                // Convert the ByteArrayOutputStream to a byte array
                val bytes = out.toByteArray()
                val jsonString = String(bytes , Charset.forName("UTF-8"))
                log.info("Serialized data: $jsonString")
                // Return the byte array
                return out.toByteArray()
            }
        }
    }


    // Define a Deserializer for DataClass. This will be used when consuming messages from Kafka.
    class DataClassDeserializer : Deserializer<DataClass> {
        override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
        override fun close() {}

        // Method for converting a byte array to a DataClass
        override fun deserialize(topic: String?, data: ByteArray?): DataClass {
            ByteArrayInputStream(data).use { input ->
                var jsonString: String? =null
                return try {
                    // Convert the byte array to a string for logging
                    jsonString = data?.let { String(it, Charset.forName("UTF-8")) }
                    log.info("Deserializing: $jsonString")
                    // Convert the JSON string to a DataClass
                    val dataClass = objectMapper.readValue(input, DataClass::class.java)
                    log.info("Deserialized data class: $dataClass")
                    // Return the DataClass
                    dataClass
                } catch (e: Exception) {
                    log.error("Could not deserialize data : {}" , jsonString , e)
                    throw e
                }
            }
        }
    }

}
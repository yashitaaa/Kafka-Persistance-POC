package com.r3.kafkademo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

//This service is responsible for executing KSQL commands on KSQL DB.

@Service
class KsqlDBService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${ksqldb.baseUrl}")
    private lateinit var ksqlDbBaseUrl: String

    // Function to execute a KSQL command on the KSQL DB
    fun executeKsqlCommand(endpoint: String, ksql: String, method: HttpMethod): String {

        // setting the Content-Type header as application/json.
        val headers = HttpHeaders()
        headers.contentType= MediaType.APPLICATION_JSON

        // Creating a map of properties to send as request body,
        // here it includes the KSQL query and a map of stream properties (which is empty in this case).
        val map = mutableMapOf("ksql" to ksql, "streamsProperties" to mutableMapOf<String, Any>())

        val entity = HttpEntity(map, headers)

        // Making a POST request to the given endpoint with the created entity,

        val response = restTemplate.postForEntity("$ksqlDbBaseUrl/$endpoint", entity, String::class.java)

        return response.body!!
    }

}
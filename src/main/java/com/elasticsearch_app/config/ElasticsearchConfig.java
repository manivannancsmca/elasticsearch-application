package com.elasticsearch_app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public JsonpMapper jsonpMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // This is the line that fixes the exact exception you are seeing:
        objectMapper.registerModule(new JavaTimeModule()); 
        return new JacksonJsonpMapper(objectMapper);
    }

    @Bean
    public RestClientTransport restClientTransport(RestClient restClient, JsonpMapper jsonpMapper) {
        return new RestClientTransport(restClient, jsonpMapper);
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport transport) {
        return new ElasticsearchClient(transport);
    }
}

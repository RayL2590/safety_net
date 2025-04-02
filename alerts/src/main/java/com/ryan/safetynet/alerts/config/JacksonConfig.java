package com.ryan.safetynet.alerts.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    /**
     * Configuration de l'ObjectMapper pour la sérialisation JSON.
     * Cela garantit que toutes les propriétés sont correctement sérialisées,
     * y compris les dates et les propriétés primitives comme l'âge.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .serializationInclusion(JsonInclude.Include.ALWAYS)
            .modules(new JavaTimeModule());
            
        ObjectMapper objectMapper = builder.build();
        
        // Configurations supplémentaires
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        
        return objectMapper;
    }
} 
package com.ryan.safetynet.alerts.repository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryan.safetynet.alerts.model.Data;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

@Component
public class DataRepository {
    private static final String DATA_FILE = "data.json";
    @Getter
    private Data data;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(DataRepository.class);

    public DataRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @PostConstruct
    public void loadData() {
        File file = new File(DATA_FILE);
        try {
            if (file.exists()) {
                this.data = objectMapper.readValue(file, Data.class);
                logger.info("Data loaded successfully from {}", DATA_FILE);
            } else {
                this.data = new Data();
                logger.warn("Data file not found. Initializing with empty data.");
            }
        } catch (IOException e) {
            this.data = new Data();
            logger.error("Failed to load data from file. Initializing with empty data.", e);
        }
    }

    public void saveData() throws IOException {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            logger.warn("Overwriting existing data file: {}", DATA_FILE);
        }
        objectMapper.writeValue(file, data);
        logger.info("Data saved successfully to {}", DATA_FILE);
    }
}

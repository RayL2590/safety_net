/**
 * Classe repository responsable du chargement et de la sauvegarde des données de l'application depuis/vers un fichier JSON.
 * Cette classe sert de couche d'accès aux données qui gère la persistance des données de l'application.
 * Elle utilise Jackson ObjectMapper pour désérialiser et sérialiser les données entre les objets Java et JSON.
 */
package com.ryan.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryan.safetynet.alerts.model.Data;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Component
public class DataRepository {
    private final Logger logger = LoggerFactory.getLogger(DataRepository.class);
    
    @Value("${data.file.path:classpath:data.json}")
    private String dataFilePath;
    
    @Getter
    private Data data;
    private final ObjectMapper objectMapper;

    /**
     * Constructeur pour DataRepository.
     * Initialise l'ObjectMapper avec JavaTimeModule pour gérer les types date/heure de Java 8
     * et le configure pour ignorer les propriétés inconnues lors de la désérialisation.
     *
     * @param objectMapper Jackson ObjectMapper pour la sérialisation/désérialisation JSON
     */
    public DataRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Définit le chemin vers le fichier de données.
     * Cette méthode doit être appelée avant loadData() pour spécifier d'où les données doivent être chargées.
     *
     * @param dataFilePath Chemin vers le fichier de données JSON
     */
    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    /**
     * Charge les données depuis le fichier JSON spécifié en mémoire.
     * Cette méthode est automatiquement appelée après l'initialisation du bean grâce à l'annotation @PostConstruct.
     * Si le fichier existe, les données sont chargées à partir de celui-ci. Sinon, un objet Data vide est créé.
     * 
     * @throws DataLoadException si une erreur survient lors de la lecture ou de l'analyse du fichier de données
     */
    @PostConstruct
    public void loadData() {
        File file = new File(dataFilePath);
        try {
            if (file.exists()) {
                // Désérialise le fichier JSON en objet Data
                this.data = objectMapper.readValue(file, Data.class);
                logger.info("Données chargées avec succès depuis {}", dataFilePath);
            } else {
                // Initialise avec des données vides si le fichier n'existe pas
                this.data = new Data();
                logger.warn("Fichier de données non trouvé à {}. Initialisation avec des données vides.", dataFilePath);
            }
        } catch (IOException e) {
            // Gère les erreurs pendant la lecture du fichier ou l'analyse JSON
            this.data = new Data();
            logger.error("Échec du chargement des données depuis le fichier {}. Initialisation avec des données vides.", dataFilePath, e);
            throw new DataLoadException("Échec du chargement des données depuis le fichier : " + dataFilePath, e);
        }
    }

    /**
     * Sauvegarde les données actuelles dans le fichier JSON spécifié.
     * Si le fichier existe déjà, il sera écrasé.
     *
     * @throws IOException si une erreur survient lors de l'écriture dans le fichier
     */
    public void saveData() throws IOException {
        File file = new File(dataFilePath);
        if (file.exists()) {
            logger.warn("Écrasement du fichier de données existant : {}", dataFilePath);
        }
        // Sérialise l'objet Data en fichier JSON
        objectMapper.writeValue(file, data);
        logger.info("Données sauvegardées avec succès dans {}", dataFilePath);
    }

    /**
     * Classe d'exception personnalisée pour les erreurs de chargement de données.
     * Cette exception est lancée lorsqu'il y a un problème lors du chargement des données depuis le fichier.
     */
    public static class DataLoadException extends RuntimeException {
        /**
         * Construit une nouvelle DataLoadException avec le message détaillé et la cause spécifiés.
         *
         * @param message le message détaillé
         * @param cause la cause de l'exception
         */
        public DataLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class DataRepository {
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
     * Charge les données depuis le fichier JSON spécifié en mémoire.
     * Cette méthode est automatiquement appelée après l'initialisation du bean grâce à l'annotation @PostConstruct.
     * Si le fichier existe, les données sont chargées à partir de celui-ci. Sinon, un objet Data vide est créé.
     * 
     * @throws DataLoadException si une erreur survient lors de la lecture ou de l'analyse du fichier de données
     */
    @PostConstruct
    public void loadData() {
        try {
            // Vérifier si le chemin est un classpath
            if (dataFilePath.startsWith("classpath:")) {
                String resourcePath = dataFilePath.substring("classpath:".length());
                log.info("Tentative de chargement depuis le classpath: {}", resourcePath);
                
                // Charger depuis le classpath
                this.data = objectMapper.readValue(
                    getClass().getClassLoader().getResourceAsStream(resourcePath),
                    Data.class
                );
                log.info("Données chargées avec succès depuis le classpath: {}", resourcePath);
            } else {
                // Charger depuis un fichier
                File file = new File(dataFilePath);
                if (file.exists()) {
                    this.data = objectMapper.readValue(file, Data.class);
                    log.info("Données chargées avec succès depuis le fichier: {}", dataFilePath);
                } else {
                    this.data = new Data();
                    log.warn("Fichier de données non trouvé à {}. Initialisation avec des données vides.", dataFilePath);
                }
            }
        } catch (IOException e) {
            // Gère les erreurs pendant la lecture du fichier ou l'analyse JSON
            this.data = new Data();
            log.error("Échec du chargement des données depuis {}. Initialisation avec des données vides. Message d'erreur: {}", 
                    dataFilePath, e.getMessage(), e);
            throw new DataLoadException("Échec du chargement des données depuis : " + dataFilePath, e);
        }
    }

    /**
     * Sauvegarde les données actuelles dans le fichier JSON spécifié.
     * Si le fichier existe déjà, il sera écrasé.
     *
     * @throws IOException si une erreur survient lors de l'écriture dans le fichier
     */
    public void saveData() throws IOException {
        String filePath;

        // Traitement spécial pour classpath
        if (dataFilePath.startsWith("classpath:")) {
            String resourcePath = dataFilePath.substring("classpath:".length());
            Resource resource = new ClassPathResource(resourcePath);
            filePath = resource.getFile().getAbsolutePath(); // Chemin physique du fichier
        } else {
            filePath = dataFilePath;
        }

        File file = new File(filePath);

        // Backup et sauvegarde
        if (file.exists()) {
            Files.copy(file.toPath(), Path.of(filePath + ".backup"), StandardCopyOption.REPLACE_EXISTING);
        }

        objectMapper.writeValue(file, data);
        log.info("Données sauvegardées dans {}", filePath);
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
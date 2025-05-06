package com.ryan.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan.safetynet.alerts.model.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du repository DataRepository")
class DataRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataRepository dataRepository;

    private static final String TEST_DATA_PATH = "test-data.json";
    private static final String TEST_BACKUP_PATH = "test-data.json.backup";
    private static final String CLASS_PATH_DATA = "classpath:data.json";

    @BeforeEach
    void setUp() {
        // Configurer le chemin du fichier de test
        ReflectionTestUtils.setField(dataRepository, "dataFilePath", TEST_DATA_PATH);
    }

    @Test
    @DisplayName("Test de chargement des données depuis un fichier existant")
    void testLoadData_WithExistingFile() throws IOException {
        // Arrange
        Data expectedData = new Data();
        // Vérifier que le fichier existe
        assertTrue(new File(TEST_DATA_PATH).exists(), "Le fichier de test devrait exister");
        when(objectMapper.readValue(any(File.class), eq(Data.class))).thenReturn(expectedData);

        // Act
        dataRepository.loadData();

        // Assert
        assertNotNull(dataRepository.getData());
        verify(objectMapper).readValue(any(File.class), eq(Data.class));
    }

    @Test
    @DisplayName("Test de chargement des données depuis un fichier inexistant")
    void testLoadData_WithNonExistingFile() {
        // Arrange
        ReflectionTestUtils.setField(dataRepository, "dataFilePath", "non-existent-file.json");

        // Act
        dataRepository.loadData();

        // Assert
        assertNotNull(dataRepository.getData());
        assertTrue(dataRepository.getData().getPersons().isEmpty());
        assertTrue(dataRepository.getData().getFireStations().isEmpty());
        assertTrue(dataRepository.getData().getMedicalRecords().isEmpty());
    }

    @Test
    @DisplayName("Test de chargement des données depuis le classpath")
    void testLoadData_FromClasspath() throws IOException {
        // Arrange
        Data expectedData = new Data();
        ReflectionTestUtils.setField(dataRepository, "dataFilePath", CLASS_PATH_DATA);
        when(objectMapper.readValue(any(InputStream.class), eq(Data.class))).thenReturn(expectedData);

        // Act
        dataRepository.loadData();

        // Assert
        assertNotNull(dataRepository.getData());
        verify(objectMapper).readValue(any(InputStream.class), eq(Data.class));
    }

    @Test
    @DisplayName("Test de sauvegarde des données")
    void testSaveData() throws IOException {
        // Arrange
        Data testData = new Data();
        ReflectionTestUtils.setField(dataRepository, "data", testData);

        // Act
        dataRepository.saveData();

        // Assert
        verify(objectMapper).writeValue(any(File.class), eq(testData));
        // Vérifier que le fichier de backup existe
        assertTrue(Files.exists(Path.of(TEST_BACKUP_PATH)), 
            "Le fichier de backup devrait être créé");
    }

    @Test
    @DisplayName("Test de sauvegarde des données avec une erreur d'IO")
    void testSaveData_WithIOException() throws IOException {
        // Arrange
        Data testData = new Data();
        ReflectionTestUtils.setField(dataRepository, "data", testData);
        doThrow(new IOException("Test exception")).when(objectMapper).writeValue(any(File.class), any(Data.class));

        // Act & Assert
        assertThrows(IOException.class, () -> dataRepository.saveData());
    }

    @Test
    @DisplayName("Test de chargement des données avec une erreur d'IO")
    void testLoadData_WithIOException() throws IOException {
        // Arrange
        when(objectMapper.readValue(any(File.class), eq(Data.class)))
            .thenThrow(new IOException("Test exception"));

        // Act & Assert
        assertThrows(DataRepository.DataLoadException.class, () -> dataRepository.loadData());
    }
}

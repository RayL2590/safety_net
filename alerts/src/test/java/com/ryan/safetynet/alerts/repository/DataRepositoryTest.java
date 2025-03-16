package com.ryan.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*; 
import static org.junit.jupiter.api.Assertions.*; 
import java.io.File;
import java.io.IOException;
import com.ryan.safetynet.alerts.model.Data;

@ExtendWith(MockitoExtension.class)
public class DataRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataRepository dataRepository;

    private final String testFilePath = "test-data.json";

    @BeforeEach
    void setUp() {
        dataRepository.setDataFilePath(testFilePath);
    }

    @Test
    void testLoadData_FileNotFound() {
        // Arrange
        dataRepository.setDataFilePath("non-existent-file.json");

        // Act
        dataRepository.loadData();

        // Assert
        assertNotNull(dataRepository.getData());
    }

    @Test
    void testLoadData_IOException() throws IOException {
        // Arrange
        File file = new File(testFilePath);
        when(objectMapper.readValue(file, Data.class)).thenThrow(new IOException("File read error"));

        // Act & Assert
        assertThrows(DataRepository.DataLoadException.class, () -> dataRepository.loadData());
    }

    @Test
    void testLoadData_Success() throws IOException {
        // Arrange
        File file = new File(testFilePath);
        Data expectedData = new Data();
        when(objectMapper.readValue(file, Data.class)).thenReturn(expectedData);

        // Act
        dataRepository.loadData();

        // Assert
        assertNotNull(dataRepository.getData());
        assertEquals(expectedData, dataRepository.getData());
    }

    @Test
    void testSaveData_IOException() throws IOException {
        // Arrange
        File file = new File(testFilePath);
        doThrow(new IOException("File write error")).when(objectMapper).writeValue(file, dataRepository.getData());

        // Act & Assert
        assertThrows(IOException.class, () -> dataRepository.saveData());
    }

    @Test
    void testIntegrationWithRealFile() throws IOException {
        // Utiliser le fichier data-test.json qui existe déjà dans les ressources
        String testDataPath = "src/main/resources/data-test.json";
        
        // Utiliser un vrai ObjectMapper (pas un mock)
        ObjectMapper realMapper = new ObjectMapper();
        realMapper.registerModule(new JavaTimeModule());
        realMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Créer un repository avec le vrai mapper
        DataRepository repository = new DataRepository(realMapper);
        repository.setDataFilePath(testDataPath);
        
        // Charger les données
        repository.loadData();
        
        // Vérifier que les données ont été correctement chargées
        assertNotNull(repository.getData());
        assertFalse(repository.getData().getPersons().isEmpty());
        assertEquals("John", repository.getData().getPersons().get(0).getFirstName());
        assertEquals("Boyd", repository.getData().getPersons().get(0).getLastName());
        
        // Vérifier que les firestations sont chargées
        assertFalse(repository.getData().getFireStations().isEmpty());
        
        // Vérifier que les dossiers médicaux sont chargés
        assertFalse(repository.getData().getMedicalRecords().isEmpty());
    }
}

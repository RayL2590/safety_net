package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final DataRepository dataRepository;
    private final Validator validator;
    private final PersonService personService;

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @return Un optional contenant le dossier médical trouvé ou vide si le dossier n'existe pas
     */
    public Optional<MedicalRecord> findMedicalRecordByName(String firstName, String lastName) {
        log.debug("Recherche du dossier médical pour {} {}", firstName, lastName);
        Optional<MedicalRecord> medicalRecord = dataRepository.getData().getMedicalRecords().stream()
                .filter(m -> m.getFirstName().equals(firstName) && m.getLastName().equals(lastName))
                .findFirst();
        log.debug("Dossier médical trouvé: {}", medicalRecord.isPresent());
        return medicalRecord;
    }

    /**
     * Ajoute un nouveau dossier médical après avoir vérifié que la personne existe.
     * 
     * @param medicalRecord Le dossier médical à ajouter
     * @return le dossier médical ajouté
     * @throws ResourceNotFoundException si la personne n'existe pas dans la base de données
     * @throws IllegalArgumentException si un dossier médical existe déjà pour cette personne
     */
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) throws IOException {
        log.info("Ajout d'un nouveau dossier médical pour {} {}", 
            medicalRecord.getFirstName(), medicalRecord.getLastName());
        
        // Vérifier d'abord si la personne existe
        if (personService.findPersonByName(medicalRecord.getFirstName(), medicalRecord.getLastName()).isEmpty()) {
            log.error("Impossible d'ajouter un dossier médical pour une personne inexistante: {} {}", 
                medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new ResourceNotFoundException(
                String.format("Personne non trouvée dans la base de données: %s %s", 
                    medicalRecord.getFirstName(), medicalRecord.getLastName()));
        }
        
        // Vérifier si un dossier médical existe déjà pour cette personne
        Optional<MedicalRecord> existingRecord = findMedicalRecordByName(
            medicalRecord.getFirstName(), medicalRecord.getLastName());
        if (existingRecord.isPresent()) {
            log.error("Un dossier médical existe déjà pour {} {}", 
                medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new IllegalArgumentException(
                String.format("Un dossier médical existe déjà pour %s %s. Utilisez la méthode PUT pour le mettre à jour.", 
                    medicalRecord.getFirstName(), medicalRecord.getLastName()));
        }
        
        var violations = validator.validate(medicalRecord);
        if (!violations.isEmpty()) {
            log.error("Erreur de validation lors de l'ajout du dossier médical: {}", violations);
            throw new ConstraintViolationException("Erreur de validation dans MedicalRecordService", violations);
        }

        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        medicalRecords.add(medicalRecord);
        dataRepository.saveData();
        log.info("Dossier médical ajouté avec succès");
        return medicalRecord;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @param medicalRecord Les nouvelles informations du dossier médical
     * @return Le dossier médical mis à jour, ou null si le dossier n'existe pas
     */
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord medicalRecord) throws IOException  {
        log.info("Mise à jour du dossier médical pour {} {}", firstName, lastName);
        
        // Vérifier d'abord si la personne existe
        if (personService.findPersonByName(firstName, lastName).isEmpty()) {
            log.error("Impossible de mettre à jour un dossier médical pour une personne inexistante: {} {}", 
                firstName, lastName);
            throw new ResourceNotFoundException(
                String.format("Personne non trouvée dans la base de données: %s %s", 
                    firstName, lastName));
        }
        
        var violations = validator.validate(medicalRecord);
        if (!violations.isEmpty()) {
            log.error("Erreur de validation lors de la mise à jour du dossier médical: {}", violations);
            throw new ConstraintViolationException("Erreur de validation lors de la mise à jour", violations);
        }

        Optional<MedicalRecord> existingMedicalRecord = findMedicalRecordByName(firstName, lastName);
        if (existingMedicalRecord.isPresent()) {
            MedicalRecord m = existingMedicalRecord.get();
            m.setBirthdate(medicalRecord.getBirthdate());
            m.setMedications(medicalRecord.getMedications());
            m.setAllergies(medicalRecord.getAllergies());
            dataRepository.saveData();
            log.info("Dossier médical mis à jour avec succès");
            return m;
        }
        log.warn("Tentative de mise à jour d'un dossier médical inexistant pour {} {}", firstName, lastName);
        return null;
    }

    /**
     * @param firstName prénom
     * @param lastName nom
     * @return true si le dossier médical a bien été supprimé, sinon false
     */
    public boolean deleteMedicalRecord(String firstName, String lastName) throws IOException {
        log.info("Suppression du dossier médical pour {} {}", firstName, lastName);
        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        boolean removed = medicalRecords.removeIf(m -> m.getFirstName().equals(firstName) && m.getLastName().equals(lastName));
        if (removed) {
            dataRepository.saveData();
            log.info("Dossier médical supprimé avec succès");
        } else {
            log.warn("Tentative de suppression d'un dossier médical inexistant pour {} {}", firstName, lastName);
        }
        return removed;
    }

    /**
     * Récupère la liste de tous les dossiers médicaux enregistrés dans le système.
     *
     * @return Liste de tous les dossiers médicaux
     */
    public List<MedicalRecord> getAllMedicalRecords() {
        log.debug("Récupération de tous les dossiers médicaux");
        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        log.debug("Nombre de dossiers médicaux trouvés: {}", medicalRecords.size());
        return medicalRecords;
    }
}

package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    private final DataRepository dataRepository;
    private final Validator validator;

    @Autowired
    public MedicalRecordService(DataRepository dataRepository, Validator validator) {
        this.dataRepository = dataRepository;
        this.validator = validator;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @return Un optional contenant le dossier médical trouvé ou vide si le dossier n'existe pas
     */
    public Optional<MedicalRecord> findMedicalRecordByName(String firstName, String lastName) {
        return dataRepository.getData().getMedicalRecords().stream()
                .filter(m -> m.getFirstName().equals(firstName) && m.getLastName().equals(lastName))
                .findFirst();
    }

    /**
     * @param medicalRecord Le dossier médical à ajouter
     * @return le dossier médical ajouté
     */
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) throws IOException {
        var violations = validator.validate(medicalRecord);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Erreur de validation dans MedicalRecordService", violations);
        }
        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        medicalRecords.add(medicalRecord);
        dataRepository.saveData();
        return medicalRecord;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @param medicalRecord Les nouvelles informations du dossier médical
     * @return Le dossier médical mis à jour, ou null si le dossier n'existe pas
     */
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord medicalRecord) throws IOException  {
        var violations = validator.validate(medicalRecord);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Erreur de validation lors de la mise à jour", violations);
        }

        Optional<MedicalRecord> existingMedicalRecord = findMedicalRecordByName(firstName, lastName);
        if (existingMedicalRecord.isPresent()) {
            MedicalRecord m = existingMedicalRecord.get();
            m.setBirthdate(medicalRecord.getBirthdate());
            m.setMedications(medicalRecord.getMedications());
            m.setAllergies(medicalRecord.getAllergies());
            dataRepository.saveData();
            return m;
        }
        return null;
    }

    /**
     * @param firstName prénom
     * @param lastName nom
     * @return true si le dossier médical a bien été supprimé, sinon false
     */
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        return medicalRecords.removeIf(m -> m.getFirstName().equals(firstName) && m.getLastName().equals(lastName));
    }
}

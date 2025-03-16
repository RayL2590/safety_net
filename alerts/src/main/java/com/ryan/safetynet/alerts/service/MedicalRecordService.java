package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    private final DataRepository dataRepository;

    @Autowired
    public MedicalRecordService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * @return la liste des dossiers médicaux
     */
    public List<MedicalRecord> getAllMedicalRecords() {
        return dataRepository.getData().getMedicalRecords();
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
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        List<MedicalRecord> medicalRecords = dataRepository.getData().getMedicalRecords();
        medicalRecords.add(medicalRecord);
        return medicalRecord;
    }

    /**
     * @param firstName Prénom
     * @param lastName Nom
     * @param medicalRecord Les nouvelles informations du dossier médical
     * @return Le dossier médical mis à jour, ou null si le dossier n'existe pas
     */
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord medicalRecord) {
        Optional<MedicalRecord> existingMedicalRecord = findMedicalRecordByName(firstName, lastName);
        if (existingMedicalRecord.isPresent()) {
            MedicalRecord m = existingMedicalRecord.get();
            m.setBirthdate(medicalRecord.getBirthdate());
            m.setMedications(medicalRecord.getMedications());
            m.setAllergies(medicalRecord.getAllergies());
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

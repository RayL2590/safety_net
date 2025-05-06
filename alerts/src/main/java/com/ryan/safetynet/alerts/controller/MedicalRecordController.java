package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.service.MedicalRecordService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.MedicalRecordInputDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur gérant les opérations CRUD sur les dossiers médicaux.
 * Ce contrôleur permet d'ajouter, mettre à jour, supprimer et consulter
 * les dossiers médicaux des personnes enregistrées dans le système.
 * Les dossiers médicaux contiennent des informations vitales comme la date de naissance,
 * les médicaments et les allergies, essentielles pour les services d'urgence.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Crée un objet MedicalRecord à partir d'un DTO.
     *
     * @param medicalRecordDTO le DTO contenant les informations du dossier médical
     * @return un objet MedicalRecord initialisé avec les données du DTO
     */
    private MedicalRecord createMedicalRecordFromDTO(MedicalRecordInputDTO medicalRecordDTO) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(medicalRecordDTO.getFirstName());
        medicalRecord.setLastName(medicalRecordDTO.getLastName());
        medicalRecord.setBirthdate(LocalDate.parse(medicalRecordDTO.getBirthdate(), dateFormatter));
        medicalRecord.setMedications(medicalRecordDTO.getMedications());
        medicalRecord.setAllergies(medicalRecordDTO.getAllergies());
        return medicalRecord;
    }

    /**
     * Ajoute un nouveau dossier médical dans le système.
     * Cette méthode crée un nouveau dossier médical à partir des informations fournies
     * dans le DTO et le persiste dans le système.
     * 
     * IMPORTANT: Cette méthode est réservée à la création de nouveaux dossiers médicaux.
     * Si un dossier médical existe déjà pour cette personne, une exception sera levée.
     * Dans ce cas, utilisez la méthode PUT pour mettre à jour le dossier existant.
     *
     * @param medicalRecordDTO DTO contenant les informations du dossier médical à créer
     * @return ResponseEntity contenant le dossier médical créé avec le statut HTTP 201 (Created)
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws IllegalArgumentException si un dossier médical existe déjà pour cette personne
     * @throws ResourceNotFoundException si la personne n'existe pas dans la base de données
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> addMedicalRecord(@Valid @RequestBody MedicalRecordInputDTO medicalRecordDTO) throws IOException {
        log.info("Ajout d'un nouveau dossier médical : {} {}",
                medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());

        MedicalRecord medicalRecord = createMedicalRecordFromDTO(medicalRecordDTO);
        MedicalRecord createdRecord = medicalRecordService.addMedicalRecord(medicalRecord);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    /**
     * Met à jour un dossier médical existant.
     * Cette méthode recherche un dossier médical par prénom et nom, puis met à jour
     * ses informations avec les nouvelles données fournies dans le DTO.
     *
     * @param medicalRecordDTO DTO contenant les nouvelles informations du dossier médical
     * @return ResponseEntity contenant le dossier médical mis à jour
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws ResourceNotFoundException si le dossier médical n'est pas trouvé
     */
    @PutMapping
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@Valid @RequestBody MedicalRecordInputDTO medicalRecordDTO) throws IOException {
        log.info("Mise à jour du dossier médical pour : {} {}",
                medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());

        MedicalRecord medicalRecord = createMedicalRecordFromDTO(medicalRecordDTO);
        MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(
                medicalRecordDTO.getFirstName(),
                medicalRecordDTO.getLastName(),
                medicalRecord
        );

        if (updatedRecord == null) {
            throw new ResourceNotFoundException(
                String.format("Dossier médical non trouvé pour mise à jour : %s %s",
                    medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName())
            );
        }

        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Supprime un dossier médical existant.
     * Cette méthode recherche et supprime un dossier médical identifié par prénom et nom.
     *
     * @param firstName prénom de la personne dont le dossier médical doit être supprimé
     * @param lastName nom de la personne dont le dossier médical doit être supprimé
     * @return ResponseEntity sans contenu avec le statut HTTP 204 (No Content) en cas de succès
     * @throws ResourceNotFoundException si le dossier médical n'est pas trouvé
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) throws IOException {
        log.info("Suppression du dossier médical pour : {} {}", firstName, lastName);

        boolean deleted = medicalRecordService.deleteMedicalRecord(firstName, lastName);

        if (!deleted) {
            throw new ResourceNotFoundException(
                String.format("Dossier médical non trouvé pour : %s %s", firstName, lastName)
            );
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère un dossier médical par prénom et nom.
     * Cette méthode permet de consulter les informations d'un dossier médical existant.
     * Utile pour les tests et la vérification des données.
     *
     * @param firstName prénom de la personne dont le dossier médical doit être récupéré
     * @param lastName nom de la personne dont le dossier médical doit être récupéré
     * @return ResponseEntity contenant le dossier médical trouvé
     * @throws ResourceNotFoundException si le dossier médical n'est pas trouvé
     */
    @GetMapping
    public ResponseEntity<MedicalRecord> getMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("Recherche du dossier médical pour : {} {}", firstName, lastName);

        Optional<MedicalRecord> medicalRecordOpt = medicalRecordService.findMedicalRecordByName(firstName, lastName);

        if (medicalRecordOpt.isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("Dossier médical non trouvé pour : %s %s", firstName, lastName)
            );
        }

        return ResponseEntity.ok(medicalRecordOpt.get());
    }

    /**
     * Récupère la liste de tous les dossiers médicaux enregistrés dans le système.
     *
     * @return ResponseEntity contenant la liste des dossiers médicaux
     */
    @GetMapping("/all")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        log.info("Récupération de tous les dossiers médicaux");
        List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();
        return ResponseEntity.ok(medicalRecords);
    }
}

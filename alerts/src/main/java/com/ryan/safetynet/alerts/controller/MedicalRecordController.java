package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.service.MedicalRecordService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.MedicalRecordInputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Contrôleur gérant les opérations CRUD sur les dossiers médicaux.
 * Ce contrôleur permet d'ajouter, mettre à jour, supprimer et consulter
 * les dossiers médicaux des personnes enregistrées dans le système.
 * Les dossiers médicaux contiennent des informations vitales comme la date de naissance,
 * les médicaments et les allergies, essentielles pour les services d'urgence.
 */
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private final MedicalRecordService medicalRecordService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Constructeur du contrôleur MedicalRecordController.
     * Injecte le service MedicalRecordService nécessaire pour gérer les opérations
     * sur les dossiers médicaux.
     *
     * @param medicalRecordService Le service responsable de la logique métier des dossiers médicaux
     */
    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

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
     * @param medicalRecordDTO DTO contenant les informations du dossier médical à créer
     * @return ResponseEntity contenant le dossier médical créé avec le statut HTTP 201 (Created)
     * @throws IOException en cas d'erreur lors de la persistance des données
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> addMedicalRecord(@Valid @RequestBody MedicalRecordInputDTO medicalRecordDTO) throws IOException {
        logger.info("Ajout d'un nouveau dossier médical : {} {}",
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
        logger.info("Mise à jour du dossier médical pour : {} {}",
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
        logger.info("Suppression du dossier médical pour : {} {}", firstName, lastName);

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
        logger.info("Recherche du dossier médical pour : {} {}", firstName, lastName);

        Optional<MedicalRecord> medicalRecordOpt = medicalRecordService.findMedicalRecordByName(firstName, lastName);

        if (medicalRecordOpt.isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("Dossier médical non trouvé pour : %s %s", firstName, lastName)
            );
        }

        return ResponseEntity.ok(medicalRecordOpt.get());
    }
}

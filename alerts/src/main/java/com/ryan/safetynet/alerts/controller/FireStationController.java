package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.PersonDTO;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.service.FireStationCoverageService;
import com.ryan.safetynet.alerts.service.FireStationService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.FireStationInputDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final Logger logger = LoggerFactory.getLogger(FireStationController.class);
    private final FireStationCoverageService fireStationCoverageService;
    private final FireStationService fireStationService;

    @Autowired
    public FireStationController(FireStationCoverageService fireStationCoverageService,
                                 FireStationService fireStationService,
                                 DataRepository dataRepository) {
        this.fireStationCoverageService = fireStationCoverageService;
        this.fireStationService = fireStationService;
    }


    /**
     * Endpoint pour récupérer les personnes couvertes par une caserne de pompiers.
     *
     * @param stationNumber Le numéro de la station de pompiers.
     * @return Une réponse JSON contenant la liste des personnes couvertes, ainsi que le décompte des adultes et des enfants.
     */
    @GetMapping
    public ResponseEntity<FireStationDTO> getPersonsCoveredByStation(@RequestParam int stationNumber) {
        // Appel du service pour récupérer les données
        FireStationDTO response = fireStationCoverageService.getPersonsCoveredByStation(stationNumber);

        // Log pour vérifier la présence de l'âge
        if (response != null && response.getPersons() != null) {
            logger.info("Nombre de personnes dans la réponse: {}", response.getPersons().size());
            
            // Log de toutes les personnes pour vérification
            for (PersonDTO person : response.getPersons()) {
                logger.info("Personne: {} {}, Âge: {}", 
                    person.getFirstName(), 
                    person.getLastName(),
                    person.getAge());
            }
            
            // Log pour afficher la structure JSON complète de la réponse
            // try {
            //     com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            //     String responseJson = mapper.writeValueAsString(response);
            //     logger.info("Structure JSON complète de la réponse: {}", responseJson);
            // } catch (Exception e) {
            //     logger.error("Erreur lors de la sérialisation JSON", e);
            // }
        }

        // Retourne la réponse avec un statut HTTP 200 (OK)
        return ResponseEntity.ok(response);
    }
    

    /**
     * Endpoint pour ajouter un nouveau mapping caserne/adresse.
     * Vérifie si le mapping existe déjà avant de le créer.
     *
     * @param fireStationDTO Les données de la caserne à ajouter
     * @return La caserne créée avec le statut HTTP 201 (Created)
     * @throws IOException En cas d'erreur lors de la persistance
     * @throws IllegalArgumentException Si le mapping existe déjà
     */
    @PostMapping
    public ResponseEntity<FireStation> addFireStation(@Valid @RequestBody FireStationInputDTO fireStationDTO) throws IOException {
        logger.info("Tentative d'ajout du mapping caserne/adresse : Station={}, Adresse={}", 
            fireStationDTO.getStation(), fireStationDTO.getAddress());
            
        // Vérification si le mapping existe déjà
        if (fireStationService.existsByAddress(fireStationDTO.getAddress())) {
            String errorMessage = String.format("Un mapping existe déjà pour l'adresse : %s", fireStationDTO.getAddress());
            logger.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
            
        FireStation fireStation = new FireStation();
        fireStation.setStation(String.valueOf(fireStationDTO.getStation()));
        fireStation.setAddress(fireStationDTO.getAddress());
        
        FireStation createdFireStation = fireStationService.addFireStation(fireStation);
        logger.info("Mapping caserne/adresse créé avec succès");
        return new ResponseEntity<>(createdFireStation, HttpStatus.CREATED);
    }

    /**
     * Met à jour une caserne de pompiers existante.
     * Cette méthode permet de modifier le numéro de station pour une adresse donnée.
     * Si la caserne n'existe pas, une exception ResourceNotFoundException est levée.
     *
     * @param fireStationDTO DTO contenant les informations de mise à jour (adresse et nouveau numéro de station)
     * @return ResponseEntity contenant la caserne mise à jour
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws ResourceNotFoundException si aucune caserne n'est trouvée pour l'adresse spécifiée
     */
    @PutMapping
    public ResponseEntity<FireStation> updateFireStation(@Valid @RequestBody FireStationInputDTO fireStationDTO) throws IOException {
        logger.info("Mise à jour du numéro de caserne pour l'adresse : {}", fireStationDTO.getAddress());
        
        FireStation fireStation = new FireStation();
        fireStation.setStation(String.valueOf(fireStationDTO.getStation()));
        fireStation.setAddress(fireStationDTO.getAddress());
        
        FireStation updatedFireStation = fireStationService.updateFireStation(fireStation);

        if (updatedFireStation == null) {
            throw new ResourceNotFoundException(
                String.format("Aucune caserne trouvée pour l'adresse : %s", fireStationDTO.getAddress())
            );
        }

        return ResponseEntity.ok(updatedFireStation);
    }

    /**
     * Supprime une ou plusieurs casernes de pompiers.
     * Cette méthode permet de supprimer soit :
     * - Une caserne spécifique par son adresse
     * - Toutes les casernes d'un numéro de station donné
     * Si aucune caserne n'est trouvée, une exception ResourceNotFoundException est levée.
     *
     * @param address adresse de la caserne à supprimer (optionnel)
     * @param station numéro de station des casernes à supprimer (optionnel)
     * @return ResponseEntity sans contenu en cas de succès
     * @throws IOException en cas d'erreur lors de la persistance des données
     * @throws ResourceNotFoundException si aucune caserne n'est trouvée pour les critères spécifiés
     * @throws IllegalArgumentException si aucun paramètre n'est fourni
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String station
    ) throws IOException {
        if (address != null) {
            logger.info("Suppression du mapping pour l'adresse : {}", address);
            boolean deleted = fireStationService.deleteFireStationByAddress(address);

            if (!deleted) {
                throw new ResourceNotFoundException(
                    String.format("Aucune caserne trouvée pour l'adresse : %s", address)
                );
            }

            return ResponseEntity.noContent().build();
        } else if (station != null) {
            logger.info("Suppression de tous les mappings pour la caserne numéro : {}", station);
            int countDeleted = fireStationService.deleteFireStationsByStation(station);

            if (countDeleted == 0) {
                throw new ResourceNotFoundException(
                    String.format("Aucun mapping trouvé pour la caserne numéro : %s", station)
                );
            }

            return ResponseEntity.noContent().build();
        } else {
            throw new IllegalArgumentException("Requête DELETE invalide : aucun paramètre fourni");
        }
    }


}
package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.service.FireStationCoverageService;
import com.ryan.safetynet.alerts.service.FireStationService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import com.ryan.safetynet.alerts.dto.FireStationInputDTO;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationCoverageService fireStationCoverageService;
    private final FireStationService fireStationService;

    /**
     * Récupère la liste de toutes les casernes de pompiers enregistrées dans le système.
     *
     * @return ResponseEntity contenant la liste des casernes de pompiers
     */
    @GetMapping("/all")
    public ResponseEntity<List<FireStation>> getAllFireStations() {
        log.info("Récupération de toutes les casernes de pompiers");
        List<FireStation> fireStations = fireStationService.getAllFireStations();
        return ResponseEntity.ok(fireStations);
    }

    /**
     * Endpoint pour récupérer les personnes couvertes par une caserne de pompiers.
     *
     * @param stationNumber Le numéro de la station de pompiers (optionnel).
     * @param firestation   Alias du numéro de la station de pompiers (optionnel).
     * @return Une réponse JSON contenant la liste des personnes couvertes, ainsi que le décompte des adultes et des enfants.
     */
    @GetMapping
    public ResponseEntity<?> getPersonsCoveredByStation(@RequestParam int stationNumber) {
        log.info("Requête GET /firestation avec station : {}", stationNumber);
        try {
            FireStationDTO response = fireStationCoverageService.getPersonsCoveredByStation(stationNumber);

            if (response.getPersons().isEmpty()) {
                log.debug("Aucune personne trouvée pour la station : {}", stationNumber);
                return ResponseEntity.ok().body(null);
            }

            log.info("Nombre de personnes trouvées : {}", response.getPersons().size());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.warn("Erreur : {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des personnes pour la station : {}", stationNumber, e);
            return ResponseEntity.internalServerError().build();
        }
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
    public ResponseEntity<?> addFireStation(@Valid @RequestBody FireStationInputDTO fireStationDTO) {
        log.info("Tentative d'ajout du mapping caserne/adresse : Station={}, Adresse={}", 
            fireStationDTO.getStation(), fireStationDTO.getAddress());
            
        try {
            if (fireStationService.existsByAddress(fireStationDTO.getAddress())) {
                String errorMessage = String.format("Un mapping existe déjà pour l'adresse : %s", fireStationDTO.getAddress());
                log.warn(errorMessage);
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(HttpStatus.CONFLICT.value(), errorMessage, null));
            }
                
            FireStation fireStation = new FireStation();
            fireStation.setStation(String.valueOf(fireStationDTO.getStation()));
            fireStation.setAddress(fireStationDTO.getAddress());
            
            FireStation createdFireStation = fireStationService.addFireStation(fireStation);
            log.info("Mapping caserne/adresse créé avec succès");
            return new ResponseEntity<>(createdFireStation, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("Erreur lors de la création du mapping caserne/adresse", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Erreur lors de la création du mapping", null));
        } catch (ConstraintViolationException e) {
            log.warn("Erreur de validation : {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 
                    "Données invalides", Map.of("error", e.getMessage())));
        }
    }

    /**
     * Met à jour une caserne de pompiers existante.
     * Cette méthode permet de modifier le numéro de station pour une adresse donnée.
     * Si la caserne n'existe pas, une exception ResourceNotFoundException est levée.
     *
     * @param fireStationDTO DTO contenant les informations de mise à jour (adresse et nouveau numéro de station)
     * @return ResponseEntity contenant la caserne mise à jour
     * @throws ResourceNotFoundException si aucune caserne n'est trouvée pour l'adresse spécifiée
     */
    @PutMapping
    public ResponseEntity<FireStation> updateFireStation(@Valid @RequestBody FireStationInputDTO fireStationDTO) {
        log.info("Mise à jour du numéro de caserne pour l'adresse : {}", fireStationDTO.getAddress());
        
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
     * @throws ResourceNotFoundException si aucune caserne n'est trouvée pour les critères spécifiés
     * @throws IllegalArgumentException si aucun paramètre n'est fourni
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String station
    ) {
        if (address != null) {
            log.info("Suppression du mapping pour l'adresse : {}", address);
            boolean deleted = fireStationService.deleteFireStationByAddress(address);

            if (!deleted) {
                throw new ResourceNotFoundException(
                    String.format("Aucune caserne trouvée pour l'adresse : %s", address)
                );
            }

            return ResponseEntity.noContent().build();
        } else if (station != null) {
            log.info("Suppression de tous les mappings pour la caserne numéro : {}", station);
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
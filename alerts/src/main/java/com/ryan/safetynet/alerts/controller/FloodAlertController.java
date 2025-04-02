package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.service.FloodAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur gérant les alertes d'inondation.
 * Ce contrôleur fournit des endpoints pour récupérer des informations sur les foyers
 * couverts par des casernes de pompiers spécifiques en cas d'inondation.
 * Il permet d'obtenir une vue d'ensemble des zones à risque et des habitants
 * qui pourraient être affectés.
 */
@RestController
@RequestMapping("/flood")
public class FloodAlertController {

    private final Logger logger = LoggerFactory.getLogger(FloodAlertController.class);
    private final FloodAlertService floodAlertService;

    /**
     * Constructeur du contrôleur FloodAlertController.
     * Injecte le service FloodAlertService nécessaire pour traiter les requêtes d'alerte d'inondation.
     *
     * @param floodAlertService Le service responsable de la logique métier des alertes d'inondation
     */
    @Autowired
    public FloodAlertController(FloodAlertService floodAlertService) {
        this.floodAlertService = floodAlertService;
    }

    /**
     * Récupère les informations sur les foyers couverts par les casernes de pompiers spécifiées.
     * Cette méthode permet d'obtenir une liste des adresses et des habitants couverts par
     * une ou plusieurs casernes de pompiers, utile en cas d'inondation pour planifier
     * les opérations de secours.
     *
     * @param stations Chaîne de caractères contenant les numéros de station séparés par des virgules
     * @return ResponseEntity contenant soit un FloodStationDTO avec les informations des foyers,
     *         soit un ErrorResponse en cas d'erreur
     * @throws ResourceNotFoundException Si une ou plusieurs stations spécifiées n'existent pas
     * @throws NumberFormatException Si le format des numéros de station est invalide
     */
    @GetMapping("/stations")
    public ResponseEntity<?> getHouseholdsByStations(@RequestParam String stations) {
        try {
            List<Integer> stationNumbers = Arrays.stream(stations.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            logger.info("Requête reçue pour les stations : {}", stationNumbers);

            FloodStationDTO response = floodAlertService.getHouseholdsByStations(stationNumbers);

            if (response.getAddresses().isEmpty()) {
                logger.info("Aucune adresse trouvée pour les stations : {}", stationNumbers);
                return ResponseEntity.ok().body(null);
            }

            logger.info("Nombre d'adresses trouvées : {}", response.getAddresses().size());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("Erreur : {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (NumberFormatException e) {
            String errorMessage = "Format de station invalide. Les stations doivent être des nombres.";
            logger.warn(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }
    }
}

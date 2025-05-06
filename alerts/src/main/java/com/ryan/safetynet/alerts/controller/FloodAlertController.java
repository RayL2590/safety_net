package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.service.FloodAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/flood")
public class FloodAlertController {

    private final FloodAlertService floodAlertService;

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

            log.info("Requête reçue pour les stations : {}", stationNumbers);

            FloodStationDTO response = floodAlertService.getHouseholdsByStations(stationNumbers);

            if (response.getAddresses().isEmpty()) {
                log.info("Aucune adresse trouvée pour les stations : {}", stationNumbers);
                return ResponseEntity.ok().body(null);
            }

            log.info("Nombre d'adresses trouvées : {}", response.getAddresses().size());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.warn("Erreur : {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (NumberFormatException e) {
            String errorMessage = "Format de station invalide. Les stations doivent être des nombres.";
            log.warn(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }
    }
}

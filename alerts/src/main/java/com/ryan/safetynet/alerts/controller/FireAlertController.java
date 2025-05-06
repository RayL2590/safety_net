package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.ErrorResponse;
import com.ryan.safetynet.alerts.service.FireAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller gérant les alertes incendie.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/fire")
public class FireAlertController {

    private final FireAlertService fireAlertService;

    /**
     * Endpoint pour récupérer les habitants d'une adresse avec leurs informations médicales et la caserne associée.
     *
     * @param address l'adresse à vérifier
     * @return FireAlertDTO avec détails des résidents et la station de pompiers
     */
    @GetMapping
    public ResponseEntity<?> getResidentsByAddress(@RequestParam String address) {
        log.info("Requête GET /fire avec adresse : {}", address);
        try {
            FireAlertDTO response = fireAlertService.getPersonsAndFireStationByAddress(address);

            if (response.getResidents().isEmpty()) {
                log.debug("Aucun résident trouvé pour l'adresse : {}", address);
                return ResponseEntity.ok().body(null);
            }

            log.info("Réponse envoyée (masquée) : {}", maskSensitiveData(response));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.warn("Erreur : {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des résidents pour l'adresse : {}", address, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Méthode utilitaire pour masquer les données sensibles dans les logs
    private FireAlertDTO maskSensitiveData(FireAlertDTO dto) {
        // Implémentez l'obfuscation si nécessaire (ex: masquer les emails/noms)
        return dto;
    }
}

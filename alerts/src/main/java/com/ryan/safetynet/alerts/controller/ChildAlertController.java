package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.service.ChildAlertService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller gérant les alertes liées aux enfants.
 * Expose l'endpoint /childAlert qui permet de récupérer la liste des enfants
 * et des autres membres du foyer à une adresse donnée.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    /**
     * Endpoint pour récupérer la liste des enfants à une adresse donnée.
     *
     * @param address L'adresse à vérifier
     * @return ResponseEntity contenant le ChildAlertDTO
     * @throws ResourceNotFoundException si aucun enfant n'est trouvé
     */
    @GetMapping
    public ResponseEntity<ChildAlertDTO> getChildrenAtAddress(@RequestParam String address) {
        log.info("Requête reçue pour les enfants à l'adresse: {}", address);
        
        // Validation explicite de l'adresse
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide");
        }
        
        ChildAlertDTO response = childAlertService.getChildrenAtAddress(address);
        
        // Vérification complète de la réponse
        if (response == null || response.getChildren() == null || response.getChildren().isEmpty()) {
            throw ResourceNotFoundException.noChildrenAtAddress(address); // Utilisation de la factory
        }
        
        log.info("Trouvé {} enfants et {} adultes à l'adresse {}", 
            response.getChildren().size(), 
            response.getHouseholdMembers().size(),
            address
        );
        
        return ResponseEntity.ok(response);
    }
}

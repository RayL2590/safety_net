package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.service.ChildAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller gérant les alertes liées aux enfants.
 * Expose l'endpoint /childAlert qui permet de récupérer la liste des enfants
 * et des autres membres du foyer à une adresse donnée.
 */
@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private final Logger logger = LoggerFactory.getLogger(ChildAlertController.class);
    private final ChildAlertService childAlertService;

    @Autowired
    public ChildAlertController(ChildAlertService childAlertService) {
        this.childAlertService = childAlertService;
    }

    /**
     * Endpoint pour récupérer la liste des enfants à une adresse donnée.
     * Retourne un ChildAlertDTO contenant :
     * - La liste des enfants (≤ 18 ans) avec leur âge
     * - La liste des autres membres du foyer (> 18 ans)
     *
     * @param address L'adresse à vérifier
     * @return ResponseEntity contenant le ChildAlertDTO avec les informations demandées
     */
    @GetMapping
    public ResponseEntity<ChildAlertDTO> getChildrenAtAddress(@RequestParam String address) {
        logger.info("Requête reçue pour les enfants à l'adresse: {}", address);
        ChildAlertDTO response = childAlertService.getChildrenAtAddress(address);
        if (response.getChildren().isEmpty()) {
            logger.info("Aucun enfant trouvé à l'adresse: {}", address);
            return ResponseEntity.ok().body(null); // retourne une réponse vide
        } else {
            logger.info("Nombre d'enfants trouvés à l'adresse {}: {}", address, response.getChildren().size());
            response.getChildren().forEach(child -> 
                logger.info("Enfant trouvé: {} {}, âge: {}", 
                    child.getFirstName(), 
                    child.getLastName(), 
                    child.getAge())
            );
        }
        
        logger.info("Nombre d'autres membres du foyer: {}", response.getHouseholdMembers().size());
        
        return ResponseEntity.ok(response);
    }
} 
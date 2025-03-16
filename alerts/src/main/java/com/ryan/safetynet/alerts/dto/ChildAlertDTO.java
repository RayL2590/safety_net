package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les enfants à une adresse donnée et les autres membres du foyer.
 * Utilisé pour l'endpoint /childAlert?address=X qui retourne une liste d'enfants
 * et les autres membres du foyer vivant à cette adresse.
 */
@Getter
@Setter
public class ChildAlertDTO {
    private List<ChildDTO> children;
    private List<HouseholdMemberDTO> householdMembers;
}

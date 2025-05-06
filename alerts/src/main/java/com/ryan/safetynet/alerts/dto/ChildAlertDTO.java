package com.ryan.safetynet.alerts.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
    @Valid
    @Size(max = 50, message = "La liste des enfants ne peut pas contenir plus de 50 éléments")
    private List<ChildDTO> children;

    @Valid
    @Size(max = 50, message = "La liste des membres du foyer ne peut pas contenir plus de 50 éléments")
    private List<HouseholdMemberDTO> householdMembers;
}

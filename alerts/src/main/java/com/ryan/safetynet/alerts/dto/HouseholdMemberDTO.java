package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant un membre du foyer (autre qu'un enfant).
 * Contient uniquement les informations d'identification de base.
 */
@Getter
@Setter
public class HouseholdMemberDTO {
    private String firstName;
    private String lastName;
}
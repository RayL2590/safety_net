package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO contenant la liste des emails des habitants d'une ville spécifique.
 * Utilisé pour l'endpoint /communityEmail?city=X qui retourne tous les emails
 * des résidents d'une ville donnée, permettant ainsi d'envoyer des alertes ou
 * des informations importantes à l'ensemble de la communauté.
 */
@Getter
@Setter
public class CommunityEmailDTO {
    @NotNull(message = "La liste des emails ne peut pas être null")
    private List<@Email(message = "Format d'email invalide") String> emails;
}
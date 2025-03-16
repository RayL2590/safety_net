package com.ryan.safetynet.alerts.dto;

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
    private List<String> emails;
}
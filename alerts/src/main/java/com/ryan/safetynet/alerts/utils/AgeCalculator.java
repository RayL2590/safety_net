package com.ryan.safetynet.alerts.utils;

import java.time.LocalDate;
import java.time.Period;

public class AgeCalculator {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private AgeCalculator() {
    }

    /**
     * Calcule l'âge d'une personne en années en comparant sa date de naissance
     * à la date actuelle.
     *
     * @param birthdate la date de naissance de la personne
     * @return l'âge de la personne en années
     */
    public static int calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}

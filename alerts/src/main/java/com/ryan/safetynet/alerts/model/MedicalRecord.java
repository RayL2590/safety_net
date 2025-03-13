package com.ryan.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class MedicalRecord {
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate birthdate;

    private List<String> medications;
    private List<String> allergies;

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                ", medications=" + medications +
                ", allergies=" + allergies +
                '}';
    }
}

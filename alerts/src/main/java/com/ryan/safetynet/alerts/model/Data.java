package com.ryan.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Data {
    @JsonProperty("persons")
    private List<Person> persons = new ArrayList<>();

    @JsonProperty("fireStations")
    private List<FireStation> fireStations = new ArrayList<>();

    @JsonProperty("medicalRecords")
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

}

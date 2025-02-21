package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchWithEmergencyCondition {

    private Boolean hasNightEmergency;
    private Double latitude;
    private Double longitude;

}

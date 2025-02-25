package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchByCategoryCondition {

    private Long categoryId;
    private Double latitude;
    private Double longitude;

    private Boolean isOpen;
    private Boolean hasNightClinic;
    private Boolean hasSundayAndHolidayClinic;

    private String hospitalName;
}

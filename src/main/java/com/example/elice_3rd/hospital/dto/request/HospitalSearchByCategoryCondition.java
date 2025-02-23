package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchByCategoryCondition {

    private Long categoryId;
    private Double latitude;
    private Double longitude;

    private boolean isOpen;
    private boolean hasNightClinic;
    private boolean hasSundayAndHolidayClinic;

    private String hospitalName;
}

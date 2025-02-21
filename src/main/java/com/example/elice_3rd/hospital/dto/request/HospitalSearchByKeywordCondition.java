package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchByKeywordCondition {

    private String keyword;
    private Boolean hasNightClinic;
    private Boolean hasSundayAndHolidayClinic;
    private Double latitude;
    private Double longitude;

    private String hospitalName;

}

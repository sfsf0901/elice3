package com.example.elice_3rd.diagnosisSubject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HospitalResponse {

    private String placeName;
    private String addressName;
    private String phone;
    private double latitude;
    private double longitude;

    public HospitalResponse(String placeName, String addressName, String phone, double latitude, double longitude) {
        this.placeName = placeName;
        this.addressName = addressName;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

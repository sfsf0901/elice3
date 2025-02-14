package com.example.elice_3rd.hospital.dto.response;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.entity.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HospitalResponse {

    private Long id;

    private String ykiho;
    private String placeName;
    private String postNumber;
    private String address;
    private String phone;
    private String homepage;
    private String latitude;
    private String longitude;

    private String diagnosisSubjectCode;
    private Long categoryId;

    private String sundayClinicInfo;
    private String holidayClinicInfo;

    private String hasNightEmergency;
    private String emergencyContact;

    private String weekdayLunchTime;
    private String saturdayLunchTime;

    private String mondayOpenTime;
    private String mondayCloseTime;
    private String tuesdayOpenTime;
    private String tuesdayCloseTime;
    private String wednesdayOpenTime;
    private String wednesdayCloseTime;
    private String thursdayOpenTime;
    private String thursdayCloseTime;
    private String fridayOpenTime;
    private String fridayCloseTime;
    private String saturdayOpenTime;
    private String saturdayCloseTime;
    private String sundayOpenTime;
    private String sundayCloseTime;

    private int distanceFromUser;

    public HospitalResponse(Hospital hospital, double distanceFromUser) {
        this.id = hospital.getId();
        this.ykiho = hospital.getYkiho();
        this.placeName = hospital.getPlaceName();
        this.postNumber = hospital.getPostNumber();
        this.address = hospital.getAddress();
        this.phone = hospital.getPhone();
        this.homepage = hospital.getHomepage();
        this.latitude = String.valueOf(hospital.getLatitude());
        this.longitude = String.valueOf(hospital.getLongitude());
        this.diagnosisSubjectCode = hospital.getDiagnosisSubject().getDiagnosisSubjectCode();
        this.categoryId = hospital.getCategory().getId();
        this.sundayClinicInfo = hospital.getSundayClinicInfo();
        this.holidayClinicInfo = hospital.getHolidayClinicInfo();
        this.hasNightEmergency = hospital.getHasNightEmergency();
        this.emergencyContact = hospital.getEmergencyContact();
        this.weekdayLunchTime = hospital.getWeekdayLunchTime();
        this.saturdayLunchTime = hospital.getSaturdayLunchTime();
        this.mondayOpenTime = hospital.getMondayOpenTime();
        this.mondayCloseTime = hospital.getMondayCloseTime();
        this.tuesdayOpenTime = hospital.getTuesdayOpenTime();
        this.tuesdayCloseTime = hospital.getTuesdayCloseTime();
        this.wednesdayOpenTime = hospital.getWednesdayOpenTime();
        this.wednesdayCloseTime = hospital.getWednesdayCloseTime();
        this.thursdayOpenTime = hospital.getThursdayOpenTime();
        this.thursdayCloseTime = hospital.getThursdayCloseTime();
        this.fridayOpenTime = hospital.getFridayOpenTime();
        this.fridayCloseTime = hospital.getFridayCloseTime();
        this.saturdayOpenTime = hospital.getSaturdayOpenTime();
        this.saturdayCloseTime = hospital.getSaturdayCloseTime();
        this.sundayOpenTime = hospital.getSundayOpenTime();
        this.sundayCloseTime = hospital.getSundayCloseTime();
        this.distanceFromUser = (int) distanceFromUser;
    }

}

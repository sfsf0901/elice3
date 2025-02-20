package com.example.elice_3rd.hospital.dto.response;

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
    private String hospitalType;
    private String hospitalName;
    private String postNumber;
    private String address;
    private String phone;
    private String homepage;
    private String latitude;
    private String longitude;

//    private String diagnosisSubjectCode;
//    private Long categoryId;

    private Boolean hasNightEmergency;
    private String emergencyContact;

    private Boolean hasNightClinic;
    private Boolean hasSundayAndHolidayClinic;

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

    private String weekdayLunchTime;
    private String saturdayLunchTime;

    private String sundayClinicInfo;
    private String holidayClinicInfo;

    private int distanceFromUser;

    public HospitalResponse(Hospital hospital, double distanceFromUser) {
        this.id = hospital.getId();
        this.ykiho = hospital.getYkiho();
        this.hospitalType = hospital.getHospitalType();
        this.hospitalName = hospital.getHospitalName();
        this.postNumber = hospital.getPostNumber();
        this.address = hospital.getAddress();
        this.phone = hospital.getPhone();
        this.homepage = hospital.getHomepage();
        this.latitude = String.valueOf(hospital.getLatitude());
        this.longitude = String.valueOf(hospital.getLongitude());

//        this.diagnosisSubjectCode = hospital.getDiagnosisSubject().getDiagnosisSubjectCode();
//        this.categoryId = hospital.getCategory().getId();

        this.hasNightEmergency = hospital.getHasNightEmergency();
        this.emergencyContact = hospital.getEmergencyContact();

        this.hasNightClinic = hospital.getHasNightClinic();
        this.hasSundayAndHolidayClinic = hospital.getHasSundayAndHolidayClinic();

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

        this.sundayClinicInfo = hospital.getSundayClinicInfo();
        this.holidayClinicInfo = hospital.getHolidayClinicInfo();

        this.distanceFromUser = (int) distanceFromUser;
    }

}

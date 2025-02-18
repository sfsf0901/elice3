package com.example.elice_3rd.hospital.entity;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.dto.HospitalDetails;
import com.example.elice_3rd.hospital.dto.HospitalInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_subject_id")
    private DiagnosisSubject diagnosisSubject;

    @Column(nullable = false)
    private String ykiho;

    private String hospitalType;
    private String placeName;
    private String postNumber;
    private String address;
    private String phone;
    private String homepage;
    private Double latitude;
    private Double longitude;

    private String hasNightEmergency;
    private String emergencyContact;

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

    public static Hospital create(HospitalInfo hospitalInfo) {
        Hospital hospital = new Hospital();
        hospital.category = hospitalInfo.getCategory();
        hospital.diagnosisSubject = hospitalInfo.getDiagnosisSubject();
        hospital.ykiho = hospitalInfo.getYkiho();
        hospital.hospitalType = hospitalInfo.getClCdNm();
        hospital.placeName = hospitalInfo.getYadmNm();
        hospital.postNumber = hospitalInfo.getPostNo();
        hospital.address = hospitalInfo.getAddr();
        hospital.phone = hospitalInfo.getTelno();
        hospital.homepage = hospitalInfo.getHospUrl();
        hospital.latitude = hospitalInfo.getLatitudeAsDouble();
        hospital.longitude = hospitalInfo.getLongitudeAsDouble();
        return hospital;
    }

    public void updateDetails(HospitalDetails details) {
        this.hasNightEmergency = details.getHasNightEmergency();
        this.emergencyContact = details.getEmergencyContact();

        this.mondayOpenTime = details.getMondayOpenTime();
        this.mondayCloseTime = details.getMondayCloseTime();
        this.tuesdayOpenTime = details.getTuesdayOpenTime();
        this.tuesdayCloseTime = details.getTuesdayCloseTime();
        this.wednesdayOpenTime = details.getWednesdayOpenTime();
        this.wednesdayCloseTime = details.getWednesdayCloseTime();
        this.thursdayOpenTime = details.getThursdayOpenTime();
        this.thursdayCloseTime = details.getThursdayCloseTime();
        this.fridayOpenTime = details.getFridayOpenTime();
        this.fridayCloseTime = details.getFridayCloseTime();
        this.saturdayOpenTime = details.getSaturdayOpenTime();
        this.saturdayCloseTime = details.getSaturdayCloseTime();
        this.sundayOpenTime = details.getSundayOpenTime();
        this.sundayCloseTime = details.getSundayCloseTime();

        this.weekdayLunchTime = details.getWeekdayLunchTime();
        this.saturdayLunchTime = details.getSaturdayLunchTime();

        this.sundayClinicInfo = details.getSundayClinicInfo();
        this.holidayClinicInfo = details.getHolidayClinicInfo();
    }
}

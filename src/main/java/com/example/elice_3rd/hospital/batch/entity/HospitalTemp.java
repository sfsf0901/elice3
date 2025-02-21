package com.example.elice_3rd.hospital.batch.entity;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.batch.dto.HospitalDetails;
import com.example.elice_3rd.hospital.batch.dto.HospitalInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
public class HospitalTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_temp_id")
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
    private String hospitalName;
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

    public static HospitalTemp create(HospitalInfo hospitalInfo) {
        HospitalTemp hospitalTemp = new HospitalTemp();
        hospitalTemp.category = hospitalInfo.getCategory();
        hospitalTemp.diagnosisSubject = hospitalInfo.getDiagnosisSubject();
        hospitalTemp.ykiho = hospitalInfo.getYkiho();
        hospitalTemp.hospitalType = hospitalInfo.getClCdNm();
        hospitalTemp.hospitalName = hospitalInfo.getYadmNm();
        hospitalTemp.postNumber = hospitalInfo.getPostNo();
        hospitalTemp.address = hospitalInfo.getAddr();
        hospitalTemp.phone = hospitalInfo.getTelno();
        hospitalTemp.homepage = hospitalInfo.getHospUrl();
        hospitalTemp.latitude = hospitalInfo.getLatitudeAsDouble();
        hospitalTemp.longitude = hospitalInfo.getLongitudeAsDouble();
        return hospitalTemp;
    }

    public void updateDetails(HospitalDetails details) {
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

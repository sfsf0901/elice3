package com.example.elice_3rd.hospital.entity;

import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table
public class Hospital extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long id;

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_subject_id")
    private DiagnosisSubject diagnosisSubject;*/

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

    public static Hospital create(HospitalTemp hospitalTemp) {
        Hospital hospital = new Hospital();
        hospital.ykiho = hospitalTemp.getYkiho();
        hospital.hospitalType = hospitalTemp.getHospitalType();
        hospital.hospitalName = hospitalTemp.getHospitalName();
        hospital.postNumber = hospitalTemp.getPostNumber();
        hospital.address = hospitalTemp.getAddress();
        hospital.phone = hospitalTemp.getPhone();
        hospital.homepage = hospitalTemp.getHomepage();
        hospital.latitude = hospitalTemp.getLatitude();
        hospital.longitude = hospitalTemp.getLongitude();

        hospital.hasNightEmergency = setHasNightEmergency(hospitalTemp.getHasNightEmergency());
        hospital.emergencyContact = hospitalTemp.getEmergencyContact();

        hospital.hasNightClinic = setHasNightClinic(hospitalTemp);
        hospital.hasSundayAndHolidayClinic = setHasSundayAndHolidayClinic(hospitalTemp);

        hospital.mondayOpenTime = hospitalTemp.getMondayOpenTime();
        hospital.mondayCloseTime = hospitalTemp.getMondayCloseTime();
        hospital.tuesdayOpenTime = hospitalTemp.getTuesdayOpenTime();
        hospital.tuesdayCloseTime = hospitalTemp.getTuesdayCloseTime();
        hospital.wednesdayOpenTime = hospitalTemp.getWednesdayOpenTime();
        hospital.wednesdayCloseTime = hospitalTemp.getWednesdayCloseTime();
        hospital.thursdayOpenTime = hospitalTemp.getThursdayOpenTime();
        hospital.thursdayCloseTime = hospitalTemp.getThursdayCloseTime();
        hospital.fridayOpenTime = hospitalTemp.getFridayOpenTime();
        hospital.fridayCloseTime = hospitalTemp.getFridayCloseTime();
        hospital.saturdayOpenTime = hospitalTemp.getSaturdayOpenTime();
        hospital.saturdayCloseTime = hospitalTemp.getSaturdayCloseTime();
        hospital.sundayOpenTime = hospitalTemp.getSundayOpenTime();
        hospital.sundayCloseTime = hospitalTemp.getSundayCloseTime();

        hospital.weekdayLunchTime = hospitalTemp.getWeekdayLunchTime();
        hospital.saturdayLunchTime = hospitalTemp.getSaturdayLunchTime();

        hospital.sundayClinicInfo = hospitalTemp.getSundayClinicInfo();
        hospital.holidayClinicInfo = hospitalTemp.getHolidayClinicInfo();

        return hospital;
    }

    private static Boolean setHasNightEmergency(String hasNightEmergency) {
        return "Y".equals(hasNightEmergency);
    }

    private static Boolean setHasNightClinic(HospitalTemp hospitalTemp) {
        String mondayCloseTime = hospitalTemp.getMondayCloseTime();
        String tuesdayCloseTime = hospitalTemp.getTuesdayCloseTime();
        String wednesdayCloseTime = hospitalTemp.getWednesdayCloseTime();
        String thursdayCloseTime = hospitalTemp.getThursdayCloseTime();
        String fridayCloseTime = hospitalTemp.getFridayCloseTime();
        String saturdayCloseTime = hospitalTemp.getSaturdayCloseTime();

        return isNightClinic(mondayCloseTime) ||
                isNightClinic(tuesdayCloseTime) ||
                isNightClinic(wednesdayCloseTime) ||
                isNightClinic(thursdayCloseTime) ||
                isNightClinic(fridayCloseTime) ||
                isNightClinic(saturdayCloseTime);
    }

    private static boolean isNightClinic(String closeTime) {
        if (closeTime == null || closeTime.isBlank()) {
            return false;
        }
        try {
            int time = Integer.parseInt(closeTime);
            return time > 1800;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Boolean setHasSundayAndHolidayClinic(HospitalTemp hospitalTemp) {
        String sundayClinicInfo = hospitalTemp.getSundayClinicInfo();
        String holidayClinicInfo = hospitalTemp.getHolidayClinicInfo();

        // 둘 중 하나라도 null이 아니고 공백이 아니면 true
        return isNotBlank(sundayClinicInfo) || isNotBlank(holidayClinicInfo);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

}

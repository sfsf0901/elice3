package com.example.elice_3rd.hospital.entity;

import lombok.Getter;

@Getter
public enum DiagnosisSubject {
    INTERNAL("01", "내과"),
    NEUROLOGY("02", "신경과"),
    PSYCHIATRY("03", "정신건강의학과"),
    SURGERY("04", "외과"),
    ORTHOPEDICS("05", "정형외과"),
    NEUROSURGERY("06", "신경외과"),
    CARDIOVASCULAR("07", "심장혈관흉부외과"),
    PLASTIC_SURGERY("08", "성형외과"),
    ANESTHESIOLOGY("09", "마취통증의학과"),
    OBSTETRICS_GYNECOLOGY("10", "산부인과"),
    OPHTHALMOLOGY("12", "안과"),
    OTORHINOLARYNGOLOGY("13", "이비인후과"),
    DERMATOLOGY("14", "피부과"),
    UROLOGY("15", "비뇨의학과"),
    RADIOLOGY("16", "영상의학과"),
    TUBERCULOSIS("20", "결핵과"),
    REHABILITATION("21", "재활의학과"),
    FAMILY_MEDICINE("23", "가정의학과"),
    EMERGENCY_MEDICINE("24", "응급의학과"),

    // 치과 관련
    DENTISTRY("49", "치과"),
    ORAL_MAXILLOFACIAL_SURGERY("50", "구강악안면외과"),
    PROSTHODONTICS("51", "치과보철과"),
    ORTHODONTICS("52", "치과교정과"),
    PEDIATRIC_DENTISTRY("53", "소아치과"),
    PERIODONTOLOGY("54", "치주과"),
    CONSERVATIVE_DENTISTRY("55", "치과보존과"),
    ORAL_MEDICINE("56", "구강내과"),
    INTEGRATIVE_DENTISTRY("61", "통합치의학과"),

    // 한방 관련
    ORIENTAL_INTERNAL("80", "한방내과"),
    ORIENTAL_GYNECOLOGY("81", "한방부인과"),
    ORIENTAL_PEDIATRICS("82", "한방소아과"),
    ORIENTAL_ENT_DERMATOLOGY("83", "한방안·이비인후·피부과"),
    ORIENTAL_PSYCHIATRY("84", "한방신경정신과"),
    ACUPUNCTURE("85", "침구과"),
    ORIENTAL_REHABILITATION("86", "한방재활의학과"),
    CONSTITUTIONAL_MEDICINE("87", "사상체질과"),
    ORIENTAL_EMERGENCY("88", "한방응급");

    private final String code;
    private final String name;

    DiagnosisSubject(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

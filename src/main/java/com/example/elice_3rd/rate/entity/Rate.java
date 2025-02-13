package com.example.elice_3rd.rate.entity;

import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id", nullable = false)
    private Long rateId;

    private String content;

    private int rate;

    private String hospitalId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Member patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Member doctor;
}

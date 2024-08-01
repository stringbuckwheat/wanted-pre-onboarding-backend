package com.wanted.recruit.common;

import com.wanted.recruit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    private String name;

    private String nation; // 국가
    private String region; // 지역

    @Builder
    public Company(String name, String nation, String region) {
        this.name = name;
        this.nation = nation;
        this.region = region;
    }
}
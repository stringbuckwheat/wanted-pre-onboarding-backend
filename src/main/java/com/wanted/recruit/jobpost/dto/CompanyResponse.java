package com.wanted.recruit.jobpost.dto;

import com.wanted.recruit.company.entity.Company;
import lombok.*;

/**
 * 회사 정보 반환 DTO
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long companyId; // 회사 pk
    private String name; // 회사명
    private String nation; // 회사가 위치한 국가
    private String region; // 지역

    public CompanyResponse(Company company) {
        this.companyId = company.getId();
        this.name = company.getName();
        this.nation = company.getNation();
        this.region = company.getRegion();
    }
}

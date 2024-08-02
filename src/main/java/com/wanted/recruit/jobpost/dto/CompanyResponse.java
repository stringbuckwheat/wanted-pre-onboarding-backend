package com.wanted.recruit.jobpost.dto;

import com.wanted.recruit.common.Company;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long companyId;
    private String name;
    private String nation;
    private String region;

    public CompanyResponse(Company company) {
        this.companyId = company.getId();
        this.name = company.getName();
        this.nation = company.getNation();
        this.region = company.getRegion();
    }
}

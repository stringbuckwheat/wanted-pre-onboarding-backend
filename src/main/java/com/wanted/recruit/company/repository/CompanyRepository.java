package com.wanted.recruit.company.repository;

import com.wanted.recruit.company.entity.Company;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * 회사 엔티티 Repository
 * Spring Data Repository만 존재
 */
public interface CompanyRepository extends Repository<Company, Long> {
    Company save(Company company);

    Optional<Company> findById(Long id);
}

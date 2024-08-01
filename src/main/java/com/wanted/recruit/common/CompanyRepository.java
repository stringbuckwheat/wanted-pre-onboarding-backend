package com.wanted.recruit.common;

import com.wanted.recruit.common.Company;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends Repository<Company, Long> {
    Company save(Company company);

    List<Company> findAll();
    Optional<Company> findById(Long id);
}

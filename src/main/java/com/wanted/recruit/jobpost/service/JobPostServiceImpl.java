package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.common.Company;
import com.wanted.recruit.exception.CompanyNotFoundException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.common.CompanyRepository;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {
    private final JobPostRepository jobPostRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public JobPostResponse save(JobPostRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(CompanyNotFoundException::new);

        JobPost jobPost = jobPostRepository.save(request.toEntity(company));

        return new JobPostResponse(jobPost);
    }

    @Override
    @Transactional
    public JobPostResponse update(JobPostUpdateRequest request, Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(JobPostNotFoundException::new);

        jobPost.update(request.getPosition(), request.getReward(), request.getContent(), request.getTechStack());

        return new JobPostResponse(jobPost);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jobPostRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> getAll() {
        return jobPostRepository.findAll().stream()
                .map(JobPostResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> search(String searchQuery) {
        return jobPostRepository.search(searchQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostDetail getDetail(Long id) {
        return jobPostRepository.getDetail(id);
    }
}

package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.Apply;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.repository.ApplyRepository;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.common.User;
import com.wanted.recruit.exception.AlreadyAppliedException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.exception.UserNotFoundException;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import com.wanted.recruit.common.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {
    private final ApplyRepository applyRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApplyResponse apply(ApplyRequest request) {
        // 지원 여부 확인
        if (applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId())) {
            throw new AlreadyAppliedException();
        }

        JobPost jobPost = jobPostRepository.findById(request.getJobPostId()).orElseThrow(JobPostNotFoundException::new);
        User user = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);

        Apply apply = applyRepository.save(new Apply(jobPost, user));
        return new ApplyResponse(apply);
    }
}

package com.wanted.recruit.apply.dto;

import com.wanted.recruit.apply.entity.Apply;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채용 공고 지원 응답용 DTO
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class ApplyResponse {
    // 지원 완료된 사용자 pk
    private Long userId;

    // 지원 완료된 사용자 이름
    private String name;

    // 해당 사용자가 지원한 채용공고 Pk
    private Long jobPostId;

    // 해당 사용자가 지원한 채용공고 포지션
    private String position;

    // 지원일시
    private LocalDateTime createDate;

    public ApplyResponse(Apply apply) {
        User user = apply.getUser();
        JobPost jobPost = apply.getJobPost();

        this.userId = user.getId();
        this.name = user.getName();
        this.jobPostId = jobPost.getId();
        this.position = jobPost.getPosition();

        this.createDate = apply.getCreatedDate();
    }
}

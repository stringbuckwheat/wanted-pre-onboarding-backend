package com.wanted.recruit.apply.dto;

import com.wanted.recruit.apply.Apply;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.common.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ApplyResponse {
    private Long userId;
    private String name;
    private Long jobPostId;
    private String position;
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

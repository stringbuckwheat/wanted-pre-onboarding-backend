package com.wanted.recruit.apply;

import com.wanted.recruit.common.BaseEntity;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.common.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_post_id"}))
@Getter
public class Apply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Apply(JobPost jobPost, User user) {
        this.jobPost = jobPost;
        this.user = user;
    }
}

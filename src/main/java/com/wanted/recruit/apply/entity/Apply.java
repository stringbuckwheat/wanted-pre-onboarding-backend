package com.wanted.recruit.apply.entity;

import com.wanted.recruit.common.entity.BaseEntity;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 지원 엔티티
 * 사용자가 특정 공고에 지원한 내역 저장
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 유일성 제약 조건: 유저와 채용공고 조합
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_post_id"}))
@Getter
public class Apply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private User user;

    public Apply(JobPost jobPost, User user) {
        this.jobPost = jobPost;
        this.user = user;
    }
}

package com.wanted.recruit.jobpost;

import com.wanted.recruit.common.BaseEntity;
import com.wanted.recruit.common.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "job_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class JobPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_post_id")
    private Long id;

    private String position; // 채용 포지션
    private int reward; // 채용 보상금
    private String content; // 채용 내용
    private String techStack; // 사용 기술

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @Builder
    public JobPost(String position, int reward, String content, String techStack, Company company) {
        this.position = position;
        this.reward = reward;
        this.content = content;
        this.techStack = techStack;
        this.company = company;
    }

    public void update(String position, int reward, String content, String techStack) {
        this.position = position;
        this.reward = reward;
        this.content = content;
        this.techStack = techStack;
    }
}

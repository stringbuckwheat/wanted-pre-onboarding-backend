package com.wanted.recruit.jobpost;

import com.wanted.recruit.common.BaseEntity;
import com.wanted.recruit.common.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "채용 포지션을 입력해주세요.")
    private String position; // 채용 포지션

    @Min(value = 0, message = "채용 보상금은 0원 이상이어야 합니다.")
    private int reward; // 채용 보상금

    @NotBlank(message = "채용 내용을 입력해주세요.")
    private String content; // 채용 내용

    @NotBlank(message = "사용 기술을 입력해주세요.")
    private String techStack; // 사용 기술

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "회사 정보를 입력해주세요.")
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

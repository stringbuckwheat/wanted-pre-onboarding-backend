package com.wanted.recruit.apply.repository;

import com.wanted.recruit.apply.Apply;
import com.wanted.recruit.common.Company;
import com.wanted.recruit.common.User;
import com.wanted.recruit.config.JpaConfig;
import com.wanted.recruit.config.QueryDslConfig;
import com.wanted.recruit.jobpost.JobPost;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, JpaConfig.class})
class ApplyRepositoryTest {
    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private TestEntityManager entityManager;

    private JobPost jobPost;
    private User user;
    private Apply apply; // persist 전

    @BeforeEach
    void setUp() {
        // JobPost
        Company company = Company.builder().name("원티드").nation("한국").region("서울").build();
        entityManager.persist(company);

        jobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();

        entityManager.persist(jobPost);

        // User
        user = new User("유저1");
        entityManager.persist(user);

        // Apply
        apply = new Apply(jobPost, user);
    }


    @Test
    @DisplayName("Apply 저장 테스트")
    void save_WhenValid_ShouldSaveApply() {
        Apply savedApply = applyRepository.save(apply);

        assertThat(savedApply.getId()).isNotNull();
        assertThat(savedApply.getUser().getName()).isEqualTo("유저1");
        assertThat(savedApply.getJobPost().getPosition()).isEqualTo("신입 백엔드 개발자");
    }

    @Test
    @DisplayName("Apply 저장 실패: 의존관계 형성 실패 - 사용자 없음")
    void save_WhenUserIsNull_ShouldThrowConstraintViolationException() {
        Apply invalidApply = new Apply(jobPost, null);

        assertThatThrownBy(() -> applyRepository.save(invalidApply))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Apply 저장 실패: 의존관계 형성 실패 - 채용공고 없음")
    void save_WhenJobPostIsNull_ShouldThrowConstraintViolationException() {
        Apply invalidApply = new Apply(null, user);

        assertThatThrownBy(() -> applyRepository.save(invalidApply))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("이미 지원한 공고인지 확인: 지원 내역 없는 경우")
    void existsByUserIdAndJobPostId_WhenNotApplied_ShouldReturnFalse() {
        boolean result = applyRepository.existsByUserIdAndJobPostId(1L, 2L);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("이미 지원한 공고인지 확인: 이미 지원한 경우")
    void existsByUserIdAndJobPostId_WhenAlreadyApplied_ShouldReturntrue() {
        entityManager.persist(apply);

        boolean result = applyRepository.existsByUserIdAndJobPostId(apply.getUser().getId(), apply.getJobPost().getId());
        assertThat(result).isEqualTo(true);
    }
}
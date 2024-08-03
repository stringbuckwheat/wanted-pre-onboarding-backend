package com.wanted.recruit.apply.repository;

import com.wanted.recruit.apply.entity.Apply;
import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.user.entity.User;
import com.wanted.recruit.common.config.JpaConfig;
import com.wanted.recruit.common.config.QueryDslConfig;
import com.wanted.recruit.jobpost.entity.JobPost;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, JpaConfig.class})
@DisplayName("ApplyRepository 테스트")
class ApplyRepositoryTest {
    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private TestEntityManager entityManager;

    private JobPost jobPost;
    private User user;
    private Apply apply; // persist 전

    /**
     * 기본적인 mock 데이터 저장
     * company, jobPost, user -> persistence
     * apply -> transient
     */
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
    @DisplayName("지원 저장: 성공 사례")
    void save_WhenValid_ShouldSaveApply() {
        Apply savedApply = applyRepository.save(apply);

        assertThat(savedApply.getId()).isNotNull(); // ID 생성 여부 확인
        assertThat(savedApply.getUser().getName()).isEqualTo("유저1"); // 필드 확인
        assertThat(savedApply.getJobPost().getPosition()).isEqualTo("신입 백엔드 개발자"); // 필드 확인
    }

    @Test
    @DisplayName("지원 저장 실패: 의존관계 형성 실패 - 사용자 없음")
    void save_WhenUserIsNull_ShouldThrowConstraintViolationException() {
        Apply invalidApply = new Apply(jobPost, null); // User 엔티티 값 없음

        assertThatThrownBy(() -> applyRepository.save(invalidApply))
                .isInstanceOf(ConstraintViolationException.class); // @NotNull 제약조건 위반
    }

    @Test
    @DisplayName("지원 저장 실패: 의존관계 형성 실패 - 채용공고 없음")
    void save_WhenJobPostIsNull_ShouldThrowConstraintViolationException() {
        Apply invalidApply = new Apply(null, user); // JobPost 값 없음

        assertThatThrownBy(() -> applyRepository.save(invalidApply))
                .isInstanceOf(ConstraintViolationException.class); // @NotNull 제약조건 위반
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
        entityManager.persist(apply); // 지원 내역 저장

        // 위에서 저장한 Apply 엔티티를 사용하여 조회
        boolean result = applyRepository.existsByUserIdAndJobPostId(apply.getUser().getId(), apply.getJobPost().getId());
        assertThat(result).isEqualTo(true);
    }
}
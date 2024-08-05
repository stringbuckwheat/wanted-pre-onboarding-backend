# 프리온보딩 백엔드 인턴십 선발과제
간단한 채용 웹 서비스 백엔드 API입니다

## 기술 스택
![SpringBoot](https://img.shields.io/badge/Springboot-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/JPA/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![QueryDsl](https://img.shields.io/badge/QueryDsl-000000?style=for-the-badge&logo=QueryDsl&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)


<details>
<summary>버전 상세 정보</summary>

- ```Java 17```  <br/>
- ```Spring Boot``` : 3.1.5 <br/>
- ```JPA``` : 3.3.2 <br/>
- ```QueryDsl``` : 5.0.0 <br/>
- ```MySQL``` : 10.3 <br/>

</details> <br/>


## 구조 및 설계
### ERD
![recruit_erd](https://github.com/user-attachments/assets/4000d543-63db-47b4-af13-930f977fa144)

### 💡 엔티티 양방향 매핑 지양
모든 엔티티를 @ManyToOne 단방향으로만 연결했습니다.

토이 프로젝트를 진행하면서 무분별한 양방향 매핑이 코드의 복잡도를 증가시키는 것을 경험했습니다.

이번 프로젝트에서 양방향 관계가 필요할 것으로 예상되는 부분은 '해당 회사의 다른 공고'를 조회하는 기능이었습니다. (Company - JobPost)    

그러나 이 한 기능을 위해 양방향 관계를 추가하는 것을 효율적이지 못하므로, QueryDsl을 사용하여 이를 해결했습니다.



### 디렉토리 구조
```
.
└── src
    ├── main
    │   ├── generated
    │   ├── java
    │   │   └── com.wanted.recruit
    │   │       ├── common // ⭐️ 모든 모듈에서 공통적으로 사용하는 코드가 담긴 디렉토리 
    │   │       │   ├── config // JPA, QueryDsl 설정 클래스
    │   │       │   ├── entity // 모든 엔티티가 extends 하는 기반 엔티티(createdDate, updatedDate 컬럼용)
    │   │       │   └── exception // 예외처리 로직
    │   │       │       ├── exception // Custom Exception
    │   │       │       └── handling // 전역 예외처리기와 예외 응답용 DTO
    │   │       │
    │   │       ├── apply // ⭐️ 도메인형 패키지 구조 채택
    │   │       │   ├── controller
    │   │       │   ├── dto
    │   │       │   ├── entity
    │   │       │   ├── repository
    │   │       │   └── service
    │   │       ├── company // 이하 패키지는 하위 디렉토리 생략 (Apply와 거의 동일) 
    │   │       ├── jobpost
    │   │       └── user
    │   └── resources
    └── test
        └── java
            └── com.wanted.recruit // ⭐️ 기능이 구현된 도메인의 모든 레이어 유닛 테스트
                └── apply
                │   ├── controller
                │   ├── repository
                │   └── service
                └── jobpost
                    ├── controller
                    ├── repository
                    └── service
```

## 구현 기능

### 채용공고 목록
* `@Transactional(readOnly = true)`를 사용하여 읽기 작업을 최적화
  * 이외 읽기 전용 메소드에도 동일하게 적용

### 채용공고 검색
* **QueryDsl**과 **DTO Projection**을 사용하여 응답 DTO 객체로 바로 매핑 → 성능 최적화, 효율성 향상
* 검색어가 null이거나 비어 있는 경우 전체 목록을 반환하여 빈 검색어 입력 시에도 적절한 결과 반환

### 채용공고 상세
* 채용공고 상세 응답용 DTO가 기본 채용 공고 정보 응답용 DTO를 상속받아 코드 중복 줄임
* **이 회사의 다른 채용공고** 기능 구현 
  * QueryDsl을 사용하여, **요청된 채용공고를 제외한 동일 회사의 다른 채용공고만** 포함하도록 구현

### 채용공고 등록
- **DTO Validation** 및 예외처리를 통해 잘못된 데이터가 비즈니스 로직으로 전달되는 것을 방지, 사용자 피드백
- Request DTO에 `toEntity()` 메소드를 구현하여 엔티티로의 변환 로직을 캡슐화

### 채용공고 수정
* 더티 체킹(dirty checking)을 통해 자동으로 데이터베이스에 반영

### 채용공고 삭제
* 엔티티 존재 여부를 `existsById` 메소드를 통해 확인 후 삭제
* 엔티티가 존재하지 않을 시 예외처리를 통해 사용자 경험 향상

### 채용공고 지원
* `existsByUserIdAndJobPostId` 메소드로 이미 지원한 기록이 있는지 확인, 중복 지원 시 예외를 던짐
* Apply 엔티티 **Unique 제약 조건** 설정
  * 중복 지원을 데이터베이스 레벨에서 한 번 더 방지

### ETC
* Repository를 implements 하여 필요한 메소드만 정의 
* 단방향 매핑에서 데이터 무결성을 유지하기 위해 `@OnDelete(action = OnDeleteAction.CASCADE)`를 활용하여 연관된 엔티티가 자동으로 삭제되도록 구현

## 예외처리
* AlreadyAppliedException, CompanyNotFoundException 등 다양한 Custom Exception으로 예외 처리 세분화 
* @RestControllerAdvice를 사용한 전역적 예외처리 

## 유닛 테스트
* 70개의 유닛테스트로 높은 테스트 커버리지 확보
  * 모든 주요 기능의 성공 시나리오와 예외 상황을 모두 테스트
  * 다양한 유효성 검사 및 예외 상황을 테스트하여 시스템의 예외 처리 로직이 정확히 동작하는지 확인
* 컨트롤러, 서비스, 리파지토리 등 모든 레이어에 대한 유닛 테스트 구현
  * Controller: MockMvc를 활용하여 HTTP 요청/응답 시뮬레이션 및 요청 DTO Validation 검사
  * Service: Mockito를 이용해 비즈니스 로직의 독립적 테스트 진행
  * Repository: @DataJpaTest를 통해 DB와의 상호작용 및 쿼리 실행 결과의 정확성 확인, Entity Validation 검사
* Parameterized Test를 통한 데이터 드리븐 테스트 → 데이터 유효성 검사가 다양한 입력에 대해 일관되게 수행되는지 확인
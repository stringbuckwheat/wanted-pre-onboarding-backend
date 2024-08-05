//package com.wanted.recruit;
//
//import com.wanted.recruit.company.entity.Company;
//import com.wanted.recruit.company.repository.CompanyRepository;
//import com.wanted.recruit.user.entity.User;
//import com.wanted.recruit.user.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class InitUserAndCompany {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CompanyRepository companyRepository;
//
//    @Test
//    void saveMockUsers() {
//        userRepository.save(new User("사용자1"));
//        userRepository.save(new User("사용자2"));
//        userRepository.save(new User("사용자3"));
//    }
//
//    @Test
//    void saveMockCompany() {
//        companyRepository.save(new Company("원티드", "한국", "서울"));
//        companyRepository.save(new Company("네이버", "한국", "판교"));
//        companyRepository.save(new Company("닌텐도", "일본", "교토"));
//    }
//}

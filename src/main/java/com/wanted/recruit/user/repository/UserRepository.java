package com.wanted.recruit.user.repository;

import com.wanted.recruit.user.entity.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link User} 엔티티용 Spring Data JPA 리파지토리 인터페이스
 */
public interface UserRepository extends Repository<User, Long> {
    User save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
}

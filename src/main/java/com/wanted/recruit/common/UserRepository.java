package com.wanted.recruit.common;

import com.wanted.recruit.common.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    User save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
}

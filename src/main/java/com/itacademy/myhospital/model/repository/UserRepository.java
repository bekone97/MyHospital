package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Page<User> findAll(Pageable pageable);
    User findUserByUsername(String username);

    User findByVerificationCode(String verificationCode);

    User findByEmail(String email);

    List<User> findByUsernameIsStartingWith(String keyword);
}

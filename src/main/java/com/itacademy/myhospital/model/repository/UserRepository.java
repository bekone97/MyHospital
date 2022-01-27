package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    public Page<User> findAll(Pageable pageable);
    public User findUserByUsername(String username);
    @Query(value = "delete from roles_users where user_id =(1?) and role_id=(2?)",nativeQuery = true)
    public void deleteRolesFromUser(Integer userId,Integer roleId);
    public User findByVerificationCode(String verificationCode);

    public User findByEmail(String email);
}

package com.supercode.supercode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supercode.supercode.po.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}

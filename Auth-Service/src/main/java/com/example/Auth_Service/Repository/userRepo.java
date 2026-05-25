package com.example.Auth_Service.Repository;

import com.example.Auth_Service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepo extends JpaRepository<User,Integer> {
    public User findByEmail(String email);
}

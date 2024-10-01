package com.smsc.management.app.user.model.repository;


import com.smsc.management.app.user.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserName(String userName);
    List<Users> findUsersByRole(String role);
}

package com.smsc.management.app.user.service;

import com.smsc.management.app.user.model.entity.Users;
import com.smsc.management.app.user.model.repository.UserRepository;
import com.smsc.management.app.user.dto.UserRoles;
import com.smsc.management.utils.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AppProperties appProperties;
    private final UserRepository userRepository;

    /**
     * Creates the root user if it doesn't already exist.
     */
    public void createRootUser() {
        Optional<Users> optionalUser = userRepository.findByUserName(appProperties.getRootUser());
        if (optionalUser.isEmpty()) {
            List<Users> usersList = userRepository.findUsersByRole(UserRoles.ROOT.getRoleName());
            if (!usersList.isEmpty()) {
                log.warn("There is already a root user assigning");
                return;
            }
            Users user = new Users();
            user.setName("ROOT");
            user.setLastName("ROOT");
            user.setRole(UserRoles.ROOT.getRoleName());
            user.setUserName(appProperties.getRootUser());
            user.setPassword(new BCryptPasswordEncoder().encode(appProperties.getRootPassword()));
            user.setStatus((short) 1);
            userRepository.save(user);
        }
    }
}

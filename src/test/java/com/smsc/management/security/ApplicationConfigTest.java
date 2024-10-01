package com.smsc.management.security;

import com.smsc.management.app.user.model.entity.Users;
import com.smsc.management.app.user.model.repository.UserRepository;
import com.smsc.management.integration.BaseIntegrationTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationConfigTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository repository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    void userDetailsServiceTest() {
        Users users = createAndGetMockUser("administrator");
        repository.save(users);
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(users.getUsername());
        assertNotNull(userDetails);
    }

    @Test
    void userDetailsServiceNotFound() {
        Users users = createAndGetMockUser("guest");
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        String userName = users.getUsername();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));
    }

    private Users createAndGetMockUser(String userName) {
        Users user = new Users();
        user.setUserName(userName);
        user.setPassword("MyPassword");
        user.setRole("Role");
        user.setStatus((short) 1);
        user.setName(userName.toUpperCase());
        user.setLastName("PAiC");
        return user;
    }
}

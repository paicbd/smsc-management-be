package com.smsc.management.security;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest()
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class SecurityConfigTest {

    @Autowired
    MockMvc api;

    private final String JWT_AUTH_MOCK_STRING = "SCOPE_openid";
    private final String AUTH_STRING_TEST = "fcb13146-ecd7-46a5-b9cb-a1e75fae9bdc";

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void accessAuthUrlWithNoHeaderTest() throws Exception {
        api.perform(get("/ws/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void authorizationParsingTest() throws Exception {
        api.perform(get("/ws")
                        .header("Authorization", AUTH_STRING_TEST)
                        .servletPath("/ws")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void authorizationParsingAnyTest() throws Exception {
        api.perform(get("/ws")
                        .header("Authorization", AUTH_STRING_TEST.replace("-", ""))
                        .servletPath("/ws")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void accessAuthNonExistingUrlTest() throws Exception {
        api.perform(get("/ws/AnotherUrl"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void accessAuthNonExistingHeaderTest() throws Exception {
        api.perform(get("/ws")
                        .header("NonExistingHeader", "")
                        .servletPath("/ws")
                )
                .andExpect(status().isOk());
    }

    @Test
    void accessDiameterResourceWithNoAuth() throws Exception {
        api.perform(get("/diameter-config/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void invalidUrlTest() throws Exception {
        api.perform(get("/balance-credit/any-url").servletPath("/balance-credit/any-url"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void return4xErrorResponseTest() throws Exception {
        api.perform(get("/balance-credit/credit-used")
                        .header("X-API-Key", "Cn62uZGdSUeGqmtVnHmI7iaji3C74bRd")
                        .servletPath("/balance-credit/credit-used")
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void testInvalidApiKeyValueTest() throws Exception {
        api.perform(get("/balance-credit/credit-used")
                        .header("X-API-Key", AUTH_STRING_TEST.substring(0, 3))
                        .servletPath("/balance-credit/credit-used")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void accessSs7ResourceWithNoAuth() throws Exception {
        api.perform(get("/ss7-gateways/refresh-setting"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuth(JWT_AUTH_MOCK_STRING)
    void nonExistingHeaderAndEmptyValueTest() throws Exception {
        api.perform(get("/balance-credit/credit-used")
                        .header("NonExistingHeader", "")
                        .servletPath("/balance-credit/credit-used")
                )
                .andExpect(status().isNotFound());
    }
}

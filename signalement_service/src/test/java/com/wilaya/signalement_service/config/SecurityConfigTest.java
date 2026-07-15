package com.wilaya.signalement_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPermitPublicEndpoints() throws Exception {
        mockMvc.perform(get("/signalements/123"))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldDenyAccessToProtectedEndpointsWithoutAuth() throws Exception {
        mockMvc.perform(get("/signalements"))
                .andExpect(status().isUnauthorized());
    }
}
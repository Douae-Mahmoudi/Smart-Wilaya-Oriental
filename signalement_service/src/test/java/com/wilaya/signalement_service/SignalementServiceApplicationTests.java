package com.wilaya.signalement_service;

import com.wilaya.signalement_service.config.TestSecurityConfig;
import com.wilaya.signalement_service.service.GenerateurIAClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class SignalementServiceApplicationTests {

    @MockBean
    private GenerateurIAClient generateurIAClient;

    @Test
    void contextLoads() {
    }
}





























































































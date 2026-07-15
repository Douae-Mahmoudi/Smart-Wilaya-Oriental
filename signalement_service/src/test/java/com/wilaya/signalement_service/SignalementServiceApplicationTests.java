package com.wilaya.signalement_service;

import com.wilaya.signalement_service.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class SignalementServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
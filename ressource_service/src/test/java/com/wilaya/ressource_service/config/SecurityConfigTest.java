package com.wilaya.ressource_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import(SecurityConfigTest.TestSecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {

        @GetMapping("/public")
        public String publicEndpoint() {
            return "public";
        }

        @PostMapping("/admin")
        public String adminEndpoint() {
            return "admin";
        }

        @GetMapping("/authenticated")
        public String authenticatedEndpoint() {
            return "authenticated";
        }
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/public").permitAll()
                            .requestMatchers(HttpMethod.POST, "/admin").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .httpBasic(withDefaults());
            return http.build();
        }
    }



    @Test
    void shouldRequireAuthForProtected() throws Exception {
        mockMvc.perform(get("/authenticated"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAdminWithWrongRole() throws Exception {
        mockMvc.perform(post("/admin"))
                .andExpect(status().isForbidden());
    }


}
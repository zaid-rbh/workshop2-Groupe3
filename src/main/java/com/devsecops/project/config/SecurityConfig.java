package com.devsecops.project.config;

import com.devsecops.project.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Spring Security configuration. It is deliberately weak in several places.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static RequestMatcher path(String pattern) {
        return PathPatternRequestMatcher.withDefaults().matcher(pattern);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception {

        // VULNERABILITY #5: user enumeration. The error tells the attacker whether the username exists.
        AuthenticationFailureHandler failureHandler = (request, response, exception) -> {
            String username = request.getParameter("username");
            boolean exists = username != null && userRepository.findByUsername(username).isPresent();
            String target = exists ? "/login?error=badpassword" : "/login?error=nouser";
            response.sendRedirect(request.getContextPath() + target);
        };

        http
            // VULNERABILITY #8: CSRF protection is disabled for the whole application.
            .csrf(csrf -> csrf.disable())

            // VULNERABILITY #22: all default security headers are disabled
            // (no X-Frame-Options, no X-Content-Type-Options, no Cache-Control, no HSTS...).
            .headers(headers -> headers.disable())

            // L'identifiant de session est changé après la connexion (protection contre la fixation de session).
            .sessionManagement(session -> session.sessionFixation(fix -> fix.changeSessionId()))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(path("/cart/**"), path("/orders/**"), path("/checkout/**"),
                                 path("/profile/**"), path("/upload/**")).authenticated()
                // VULNERABILITY #11: /admin/** only requires a logged-in user, NOT the ADMIN role.
                .requestMatchers(path("/admin/**")).authenticated()
                .anyRequest().permitAll())

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureHandler(failureHandler)
                .permitAll())

            .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
            .map(u -> org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRole())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /** Mots de passe hachés avec BCrypt (haché salé et lent). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

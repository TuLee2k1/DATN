package poly.com.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
         .csrf(AbstractHttpConfigurer::disable)
         // Cấu hình SecurityContext
         .securityContext(security -> security
          .securityContextRepository(securityContextRepository())
          .requireExplicitSave(false)
         )
         .authorizeHttpRequests(auth -> auth
          .requestMatchers(
           "/auth/**",
           "/login/**",
           "/logout",
           "/v3/**",
           "**",
           "/swagger-ui/**"
          ).permitAll()
          .requestMatchers("/Company/**").hasRole("COMPANY")
          .anyRequest().authenticated()
         )
         // Cấu hình Session
         .sessionManagement(session -> session
          .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
          .maximumSessions(1)
          .maxSessionsPreventsLogin(false)
          .expiredUrl("/login?expired")
          .and()
          .sessionFixation().migrateSession()
          .enableSessionUrlRewriting(false)
          .invalidSessionUrl("/login")
         )
         // Cấu hình Form Login
         .formLogin(form -> form
          .loginProcessingUrl("/authenticate")
          .loginPage("/login")
          .defaultSuccessUrl("/dashboard", true)
          .failureUrl("/login?error=true")
          .permitAll()
         )
         // OAuth2 Login
         .oauth2Login(oauth2 -> oauth2
          .defaultSuccessUrl("/loginSuccess", true)
          .failureUrl("/loginFailure")
         )
         // Cấu hình Logout
         .logout(logout -> logout
          .logoutUrl("/logout")
          .logoutSuccessUrl("/login")
          .invalidateHttpSession(true)
          .deleteCookies("JSESSIONID")
          .clearAuthentication(true)
          .addLogoutHandler((request, response, authentication) -> {
              SecurityContextHolder.clearContext();
          })
         )
         .authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }



}
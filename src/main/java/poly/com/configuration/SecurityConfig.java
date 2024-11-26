package poly.com.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
         .cors(cors -> cors.configurationSource(corsConfigurationSource()))
         .authorizeHttpRequests(auth -> auth
          // Cho phép tài nguyên tĩnh
          .requestMatchers(
           "/static/**",
           "/css/**",
           "/js/**",
           "/images/**",
           "/assets/**",
           "/webjars/**"
          ).permitAll()

          // Các URL public
          .requestMatchers(
           "/auth/**",
           "/login/**",
           "/logout",
           "/v3/**",
           "/swagger-ui/**",
           "/JobPost/**"
          ).permitAll()

          // URL yêu cầu role cụ thể
          .requestMatchers("/Company/**").hasRole("COMPANY")
          .anyRequest().authenticated()
         )
         // Giữ nguyên các cấu hình khác...
         .sessionManagement(session -> session
          .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
          .maximumSessions(1)
          .maxSessionsPreventsLogin(true)
         )
         .formLogin(form -> form
          .loginProcessingUrl("/authenticate")
          .loginPage("/login")
          .defaultSuccessUrl("/dashboard", true)
          .failureUrl("/login?error=true")
          .permitAll()
         )
         .oauth2Login(oauth2 -> oauth2
          .defaultSuccessUrl("/loginSuccess", true)
          .failureUrl("/loginFailure")
         )
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

    // CORS Configuration
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


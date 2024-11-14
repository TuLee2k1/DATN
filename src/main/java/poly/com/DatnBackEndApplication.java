package poly.com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import poly.com.Enum.RoleType;
import poly.com.model.Role;
import poly.com.repository.RoleRepository;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
public class DatnBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatnBackEndApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByRole(RoleType.ROLE_USER).isEmpty()) {
                roleRepository.save(Role.builder().role(RoleType.ROLE_USER).build());
            }
            if (roleRepository.findByRole(RoleType.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(Role.builder().role(RoleType.ROLE_ADMIN).build());
            }
            if (roleRepository.findByRole(RoleType.ROLE_COMPANY).isEmpty()) {
                roleRepository.save(Role.builder().role(RoleType.ROLE_COMPANY).build());
            }
        };
    }
}

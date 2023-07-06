package uos.capstone.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uos.capstone.dms.repository.RefreshTokenRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "uos.capstone.dms", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RefreshTokenRepository.class}))
public class DmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmsApplication.class, args);
	}
}

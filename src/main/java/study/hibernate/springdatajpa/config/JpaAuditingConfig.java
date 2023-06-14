package study.hibernate.springdatajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import study.hibernate.entity.audit.version7.StringTypeAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "myAuditorAwareConfig") // Bean 이름
public class JpaAuditingConfig {

	@Bean
	AuditorAware<String> myAuditorAwareConfig() {
		return new StringTypeAuditorAware();
	}

	// Spring Security 사용시 설정. @CreatedBy, @LastModifiedBy가 사용된 엔티티 필드는 Long 타입이여야함
//	@Bean
//	AuditorAware<Long> myAuditorAwareConfig2() {
//		return () -> {
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			if (null == authentication || !authentication.isAuthenticated()) {
//				return Optional.empty();
//			}
//			LoginUser user = (LoginUser) authentication.getPrincipal();
//			
//			return Optional.of(user.getUserid());
//		};
//	}

}
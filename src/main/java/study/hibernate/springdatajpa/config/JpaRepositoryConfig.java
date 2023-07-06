package study.hibernate.springdatajpa.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// 부트에서는 생략해도 자동설정됨
// basePackages 속성을 주지 않으면 @SpringBootApplication에 설정한 빈 scan 범위와 동일한 범위로 빈을 scan
@EnableJpaRepositories
public class JpaRepositoryConfig {

}

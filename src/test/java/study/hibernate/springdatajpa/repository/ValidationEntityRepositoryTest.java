package study.hibernate.springdatajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import study.hibernate.entity.ValidationEntity;

@SpringBootTest
@ActiveProfiles("local")
public class ValidationEntityRepositoryTest {

	@Autowired
	private ValidationEntityRepository repo;

	@Test
	void test() {
		ValidationEntity entity = ValidationEntity.builder().id(1).notNullCol("init").nullableFalseCol("init").build();
		ValidationEntity saved = repo.save(entity);
	}
}

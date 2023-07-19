package study.hibernate.springdatajpa.specification;

import javax.persistence.criteria.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import study.hibernate.entity.SpecificationTest;
import study.hibernate.entity.SpecificationTest.YesOrNo;
import study.hibernate.springdatajpa.repository.SpecificationTestRepository;

@SpringBootTest
@ActiveProfiles("local")
public class SpecificationTestRepositoryTest {

	@Autowired
	private SpecificationTestRepository repo;

	@Test
	void findAllTest() {
		
		Specification<SpecificationTest> spec = (root, query, criteriaBuilder) -> {
			// string_field like "%test"
			Predicate like = criteriaBuilder.like(root.get("stringField"), "%test%");
			
			// yes_or_no = 'YES'
			Predicate equal1 = criteriaBuilder.equal(root.get("yesOrNo"), YesOrNo.valueOf("YES"));
			
			// integer_field = 3
			Predicate equal2 = criteriaBuilder.equal(root.get("integerField"), 3);
			
			return criteriaBuilder.and(like, equal1, equal2);
		};
		
		repo.findAll(spec);
	}

}

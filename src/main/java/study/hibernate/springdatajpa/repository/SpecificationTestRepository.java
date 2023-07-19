package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.SpecificationTest;

@NoRepositoryBean
public interface SpecificationTestRepository extends JpaRepository<SpecificationTest, Long>, JpaSpecificationExecutor<SpecificationTest> {

}

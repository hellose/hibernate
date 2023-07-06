package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.ValidationEntity;

@NoRepositoryBean
public interface ValidationEntityRepository extends JpaRepository<ValidationEntity, Integer> {

}

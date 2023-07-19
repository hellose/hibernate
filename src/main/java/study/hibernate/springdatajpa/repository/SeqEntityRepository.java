package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.SeqEntity;

@NoRepositoryBean
public interface SeqEntityRepository extends JpaRepository<SeqEntity, Integer> {

}

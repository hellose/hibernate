package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.SeqEntity;

public interface SeqEntityRepository extends JpaRepository<SeqEntity, Integer> {

}

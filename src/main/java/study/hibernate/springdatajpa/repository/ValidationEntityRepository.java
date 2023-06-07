package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.ValidationEntity;

public interface ValidationEntityRepository extends JpaRepository<ValidationEntity, Integer> {

}
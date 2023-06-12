package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {

}

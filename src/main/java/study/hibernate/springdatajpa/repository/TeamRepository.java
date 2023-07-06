package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.Team;

@NoRepositoryBean
public interface TeamRepository extends JpaRepository<Team, Integer> {

}

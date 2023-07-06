package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.Member;

@NoRepositoryBean
public interface MemberRepository extends JpaRepository<Member, Integer> {

}

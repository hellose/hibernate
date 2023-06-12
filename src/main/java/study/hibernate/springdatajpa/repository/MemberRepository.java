package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

}

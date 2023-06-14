package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.version4and5.AuditVersion4;

public interface AuditVersion4Repository extends JpaRepository<AuditVersion4, Integer> {

}

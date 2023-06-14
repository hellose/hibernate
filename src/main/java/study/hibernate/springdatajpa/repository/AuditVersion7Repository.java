package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.version7.AuditVersion7;

public interface AuditVersion7Repository extends JpaRepository<AuditVersion7, Integer> {

}

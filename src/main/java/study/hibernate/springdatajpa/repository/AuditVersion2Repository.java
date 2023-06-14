package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.AuditVersion2;

public interface AuditVersion2Repository extends JpaRepository<AuditVersion2, Integer> {

}

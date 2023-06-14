package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.AuditVersion3;

public interface AuditVersion3Repository extends JpaRepository<AuditVersion3, Integer> {

}

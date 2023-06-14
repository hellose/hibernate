package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.version6.AuditVersion6;

public interface AuditVersion6Repository extends JpaRepository<AuditVersion6, Integer> {

}

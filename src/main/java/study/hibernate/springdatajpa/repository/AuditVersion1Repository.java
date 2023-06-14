package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.hibernate.entity.audit.version1.AuditVersion1;

public interface AuditVersion1Repository extends JpaRepository<AuditVersion1, Integer> {

}

package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version3.AuditVersion3;

@NoRepositoryBean
public interface AuditVersion3Repository extends JpaRepository<AuditVersion3, Integer> {

}

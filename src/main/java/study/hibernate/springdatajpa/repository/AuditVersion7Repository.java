package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version7.AuditVersion7;

@NoRepositoryBean
public interface AuditVersion7Repository extends JpaRepository<AuditVersion7, Integer> {

}

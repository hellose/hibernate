package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version4and5.AuditVersion4;

@NoRepositoryBean
public interface AuditVersion4Repository extends JpaRepository<AuditVersion4, Integer> {

}

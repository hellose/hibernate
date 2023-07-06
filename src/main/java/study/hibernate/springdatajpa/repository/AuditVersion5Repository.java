package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version4and5.AuditVersion5;

@NoRepositoryBean
public interface AuditVersion5Repository extends JpaRepository<AuditVersion5, Integer> {

}

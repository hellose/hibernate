package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version6.AuditVersion6;

@NoRepositoryBean
public interface AuditVersion6Repository extends JpaRepository<AuditVersion6, Integer> {

}

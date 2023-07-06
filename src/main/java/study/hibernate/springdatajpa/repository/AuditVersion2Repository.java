package study.hibernate.springdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import study.hibernate.entity.audit.version2.AuditVersion2;

@NoRepositoryBean
public interface AuditVersion2Repository extends JpaRepository<AuditVersion2, Integer> {

}

package study.hibernate.springdatajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import study.hibernate.entity.audit.AuditVersion1;
import study.hibernate.entity.audit.AuditVersion2;
import study.hibernate.entity.audit.AuditVersion3;

@SpringBootTest
@ActiveProfiles("test")
@Rollback(false)
@Transactional
public class AuditVersionRepositoryTest {

	@Autowired
	private AuditVersion1Repository v1Repo;

	@Autowired
	private AuditVersion2Repository v2Repo;
	
	@Autowired
	private AuditVersion3Repository v3Repo;

	@Test
	void auditVersion1EntitySaveTest() {
		AuditVersion1 entity = new AuditVersion1();
		AuditVersion1 saved = v1Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void auditVersion2EntitySaveTest() {
		AuditVersion2 entity = new AuditVersion2();
		AuditVersion2 saved = v2Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void auditVersion3EntitySaveTest() {
		AuditVersion3 entity = new AuditVersion3();
		AuditVersion3 saved = v3Repo.save(entity);
		saved.setCol("update");
	}
}

package study.hibernate.springdatajpa.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import study.hibernate.entity.audit.version1.AuditVersion1;
import study.hibernate.entity.audit.version2.AuditVersion2;
import study.hibernate.entity.audit.version3.AuditVersion3;
import study.hibernate.entity.audit.version4and5.AuditVersion4;
import study.hibernate.entity.audit.version4and5.AuditVersion5;
import study.hibernate.entity.audit.version6.AuditVersion6;
import study.hibernate.entity.audit.version7.AuditVersion7;
import study.hibernate.springdatajpa.repository.AuditVersion1Repository;
import study.hibernate.springdatajpa.repository.AuditVersion2Repository;
import study.hibernate.springdatajpa.repository.AuditVersion3Repository;
import study.hibernate.springdatajpa.repository.AuditVersion4Repository;
import study.hibernate.springdatajpa.repository.AuditVersion5Repository;
import study.hibernate.springdatajpa.repository.AuditVersion6Repository;
import study.hibernate.springdatajpa.repository.AuditVersion7Repository;

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

	@Autowired
	private AuditVersion4Repository v4Repo;

	@Autowired
	private AuditVersion5Repository v5Repo;

	@Autowired
	private AuditVersion6Repository v6Repo;

	@Autowired
	private AuditVersion7Repository v7Repo;

	/*
	 * Date Auditing 테스트
	 */
	@Test
	void test1() {
		AuditVersion1 entity = new AuditVersion1();
		AuditVersion1 saved = v1Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void test2() {
		AuditVersion2 entity = new AuditVersion2();
		AuditVersion2 saved = v2Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void test3() {
		AuditVersion3 entity = new AuditVersion3();
		AuditVersion3 saved = v3Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void test4() {
		AuditVersion4 entity = new AuditVersion4();
		AuditVersion4 saved = v4Repo.save(entity);
		saved.setCol("update");
	}

	@Test
	void test5() {
		AuditVersion5 entity = new AuditVersion5();
		AuditVersion5 saved = v5Repo.save(entity);
		saved.setCol("update");
	}

	/*
	 * Entity에 등록된 Entity Listener가 여러개인경우
	 */
	@Test
	void test6() {
		try {
			AuditVersion6 entity = new AuditVersion6();
			AuditVersion6 saved = v6Repo.save(entity);
			saved.setCol("update");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * @CreatedBy, @LastModifiedBy 테스트
	 */
	@Test
	void test7() {
		try {
			AuditVersion7 entity = new AuditVersion7();
			AuditVersion7 saved = v7Repo.save(entity);
			saved.setCol("first update");
			saved.setCol("second update");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

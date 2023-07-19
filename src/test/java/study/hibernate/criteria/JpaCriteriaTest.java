package study.hibernate.criteria;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.CriteriaTest;

@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class JpaCriteriaTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	@BeforeEach
	void beforeEach() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				for (int i = 1; i <= 10; i++) {
					em.persist(new CriteriaTest());
				}

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
				tx = null;
				em = null;
				emf = null;
			}
		});
	}

	@AfterEach
	void afterEach() {
		tx = null;
		em = null;
		emf = null;
	}

	@Test
	@DisplayName("select * from 테이블")
	void selectClauseForAll() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// CriteriaBuilder 를 얻어서 Criteria 를 사용할 준비
				CriteriaBuilder cb = em.getCriteriaBuilder();

				// 빌더로 부터 CriteriaQuery 획득
				CriteriaQuery<CriteriaTest> cq = cb.createQuery(CriteriaTest.class);

				// FROM 절을 생성, 반환된 값 root는 Criteria에서 사용하는 특별한 별칭, root를 조회의 시작점이라는 의미로 쿼리 루트(Root)로 사용
				Root<CriteriaTest> root = cq.from(CriteriaTest.class);
				cq.select(root);

				// createQuery 로부터 TypedQuery 를 얻어서 결과를 얻는다
				TypedQuery<CriteriaTest> query = em.createQuery(cq);
				List<CriteriaTest> list = query.getResultList();

				log.debug("Query Result Size: {}", list.size());
				
				for(CriteriaTest item: list) {
					log.debug("{}", em.contains(item));
				}
				
				// 영속성 컨텎스트에 올라와있음
				for (int i = 1; i <= 10; i++) {
					log.debug("===> find");
					em.find(CriteriaTest.class, (long) i);
				}

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

	@Test
	@DisplayName("select 컬럼1 from 테이블")
	void selectClauseSingleColumn() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<CriteriaTest> root = cq.from(CriteriaTest.class);
				
				cq.select(root.get("id"));

				TypedQuery<Long> query = em.createQuery(cq);
				List<Long> list = query.getResultList();
				log.debug("Query Result Size: {}", list.size());
				
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}
	
	@Test
	@DisplayName("select 컬럼1,컬럼2 from 테이블")
	void selectClauseMultipleColumn() {
		//TODO
		fail("TODO");
	}

	@Test
	void orderByClause() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CriteriaTest> cq = cb.createQuery(CriteriaTest.class);

				Root<CriteriaTest> root = cq.from(CriteriaTest.class);

				// javax.persistence.criteria.Order
				Order idDesc = cb.desc(root.get("id"));
				Order testintegerAsc = cb.asc(root.get("testinteger"));

				// 가변인자 대신 List 파라메터 메서드도 존재함
//				cq.select(root).orderBy(idDesc, testintegerAsc);
				cq.select(root).orderBy(testintegerAsc, idDesc);

				TypedQuery<CriteriaTest> query = em.createQuery(cq);
				query.getResultList();

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

	@Test
	void whereClause() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CriteriaTest> cq = cb.createQuery(CriteriaTest.class);

				Root<CriteriaTest> root = cq.from(CriteriaTest.class);

				Predicate testintegerEqual = cb.equal(root.get("testinteger"), 1);
				Predicate teststringEqual = cb.equal(root.get("teststring"), "test");

				// 가변인자 대신 List 파라메터 메서드도 존재함
				cq.select(root).where(testintegerEqual, teststringEqual);

				TypedQuery<CriteriaTest> query = em.createQuery(cq);
				query.getResultList();

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

}

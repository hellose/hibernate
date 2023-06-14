package study.hibernate.dynamic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.dynamic.DynamicInsertEntity;

public class DynamicInsertEntityTest {

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

	/*
	 * @DynamicInsert 적용 X
	 */
	@Test
	void notApplyDynamicInsert() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				DynamicInsertEntity entity = new DynamicInsertEntity();
				// insert into dynamic_test_entity
				// (def_not_null, def_nullable, nullable, id) <- 모든 컬럼 정의됨
				// values
				// (null, null, null, 1); <- 필드값이 null이면 쿼리에 null로 들어감. column default값 적용안됨
				em.persist(entity);

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

	/*
	 * @DynamicInsert 적용 O
	 */
	@Test
	void applyDynamicInsert() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				DynamicInsertEntity entity = new DynamicInsertEntity();
				// insert into dynamic_test_entity
				// (id) <- null이 아닌 필드에 해당하는 컬럼만 정의됨
				// values
				// (1); <- column default 값 적용됨
				em.persist(entity);

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

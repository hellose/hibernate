package study.hibernate.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.ValidationEntity;

/**
 * Column어노테이션 nullable=false 테스트
 */
public class ColumnNullableFalseTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	@BeforeEach
	void init() {
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
				System.out.println("===> beforeEach");
			}
		});
	}

	/**
	 * persist시점에 notnull인 경우 - ok
	 */
	@Test
	@DisplayName("persist시점에 notnull인 경우 ok")
	void nullableFalseTest1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				ValidationEntity entity = ValidationEntity.builder().id(2).notNullCol("init").nullableFalseCol("init")
						.build();

				System.out.println("===> persist with notnull");
				em.persist(entity);

				System.out.println("===> flush");
				em.flush();

				System.out.println("===> commit");
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

	/**
	 * persist시점에 null이지만 flush호출전 notnull로 초기화된 경우
	 */
	@Test
	void nullableFalseTest2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				ValidationEntity entity = ValidationEntity.builder().id(1).notNullCol("init").nullableFalseCol(null)
						.build();

				System.out.println("===> persist with null");
				em.persist(entity);

				entity.setNullableFalseCol("init");

				System.out.println("===> flush");
				em.flush();

				System.out.println("===> commit");
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

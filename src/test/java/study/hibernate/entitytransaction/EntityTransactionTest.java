package study.hibernate.entitytransaction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Person;

/**
 * Hibernate Transaction Test
 */
public class EntityTransactionTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	@BeforeEach
	void beforeEach() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
				em = emf.createEntityManager();
				EntityTransaction tx;
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
				System.out.println("===> beforeEach");
			}
		});
	}

	@AfterEach
	void afterEach() {
		em = null;
		emf = null;
		tx = null;
		System.out.println("===> afterEach");
	}

	/**
	 * Transaction의 state 체크: begin과 commit/rollback 사이에서만 isActive가 true. 이외에 false
	 */
	@Test
	void isActiveTest() {
		System.out.println(emf);
		System.out.println(em);
		System.out.println(tx);
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();

				tx = em.getTransaction();
				System.out.println("===> getTransaction: " + tx.isActive()); // false

				tx.begin();
				System.out.println("===> begin: " + tx.isActive()); // true

//				tx.rollback();
//				System.out.println("rollback : " + tx.isActive()); // false

				tx.commit();
				System.out.println("===> commit active: " + tx.isActive()); // false

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

	/**
	 * isActive가 false인 Transaction에 대해서는 rollback호출시 내부적으로 아무런 작업 없이 리턴함
	 */
	@Test
	void rollbackDoNotAnythingWhenTransactionStateIsActiveFalse() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				
				// isActive가 false이기 때문에 rollback()은 아무런 작업없이 리턴
				tx.rollback();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

	/**
	 * 시작되지 않은 트랜잭션 commit시 예외
	 */
	@Test
	void nonBeginnedTransactionCommitExceptionOccur() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();

				// java.lang.IllegalStateException: Transaction not successfully started
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

	/**
	 * 닫힌 Session에 대해서는 Transaction을 begin할 수 없음
	 */
	@Test
	void beginTransactionExceptionWhenSessionClosed() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				
				em.close();
				
				tx = em.getTransaction();

				// java.lang.IllegalStateException: Cannot begin Transaction on closed Session/EntityManager
//				tx.begin();

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				emf.close();
			}
		});
	}

	/**
	 * 각 insert query를 개별 트랜잭션으로 처리
	 */
	@Test
	void relationBetweenPersistentContextAndTransaction() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				
				try {
					tx.begin();
					Person person1 = Person.builder().id(1).build();
					em.persist(person1);
					tx.commit();
				}catch (Exception e) {
					tx.rollback();
					throw new RuntimeException(e);
				}
				
				Thread.sleep(5000);
				
				try {
					tx.begin();
					Person person2 = Person.builder().id(2).build();
					em.persist(person2);
					tx.commit();
				}catch (Exception e) {
					tx.rollback();
					throw new RuntimeException(e);
				}
				
				em.clear();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}

}
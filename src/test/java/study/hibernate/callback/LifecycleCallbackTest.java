package study.hibernate.callback;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.CallbackEntity;

@Slf4j
public class LifecycleCallbackTest {

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
	 * @PrePersist, @PostPersist
	 */
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				CallbackEntity entity = CallbackEntity.builder()
														.id(1)
														.build();
				
				log.debug("===> persist 전");
				em.persist(entity);
				log.debug("===> persist 후");
				
				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
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
	 * @PreUpdate, @PostUpdate
	 */
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				CallbackEntity entity = CallbackEntity.builder()
														.id(1)
														.build();
				
				log.debug("===> persist 전");
				em.persist(entity);
				log.debug("===> persist 후");
				
				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
				log.debug("===> 테스트에서 필드 값 변경");
				entity.setCol("테스트에서 필드 값 변경");
				
				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
				entity.setCol("preUpdate에서 필드 값 변경");
				
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
	 * insert, update 쿼리를 한번에 flush하는 경우: @PreUpdate -> insert -> @PostPersist -> update -> @PostUpdate
	 * (@PreUpdate는 모든 쿼리 발생 전 호출됨)
	 */
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				CallbackEntity entity = CallbackEntity.builder()
						.id(1)
						.build();
				
				em.persist(entity);
				entity.setCol("update1");
				
				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
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

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

				CallbackEntity entity = CallbackEntity.builder()
														.id(1)
														.col("beforeEach")
														.build();
				em.persist(entity);
				
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

	// persist -> @PrePersist, @PostPersist
	@Test
	void persist() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CallbackEntity entity = CallbackEntity.builder()
														.id(2)
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

	// merge시 DB에 row가 존재하지 않는 경우 -> @PrePersist, @PostPersist
	@Test
	void mergeWhenDbDoesntHaveRow() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CallbackEntity entity = CallbackEntity.builder()
														.id(2)
														.build();

				log.debug("===> merge 전");
				em.merge(entity);
				log.debug("===> merge 후");

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

	// 필드 변경 후 flush -> @PreUpdate, @PostUpdate
	@Test
	void managedEntityFieldUpdate() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				CallbackEntity entity = CallbackEntity.builder()
														.id(2)
														.build();

				log.debug("===> persist 전");
				em.persist(entity);
				log.debug("===> persist 후");

				log.debug("===> flush 전");
				em.flush();
				log.debug("===> flush 후");
				
				entity.setCol("update1");
				
				log.debug("===> commit 전");
				tx.commit();
				log.debug("===> commit 후");
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

	// DB에서 엔티티 조회하여 영속성 컨텍스트에 로드되는 경우 -> @PostLoad
	@Test
	void findEntity() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				log.debug("===> find 전");
				em.find(CallbackEntity.class, 1);
				log.debug("===> find 후");
				
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
	
	// 엔티티 삭제 -> @PreRemove, @PostRemove
	@Test
	void remove() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				CallbackEntity find = em.find(CallbackEntity.class, 1);
				
				log.debug("===> remove 전");
				em.remove(find);
				log.debug("===> remove 후");
				
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

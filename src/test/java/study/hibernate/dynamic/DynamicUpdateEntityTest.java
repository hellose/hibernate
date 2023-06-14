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
import study.hibernate.entity.dynamic.DynamicUpdateEntity;

/*
 * @DynamicUpdate 어노테이션 테스트
 */
public class DynamicUpdateEntityTest {

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

	@Test
	void test() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				DynamicUpdateEntity entity = DynamicUpdateEntity.builder()
																.defNullable("init")
																.defNotNull("init")
																.nullable("init")
																.build();
				em.persist(entity);
				em.flush();
				em.clear();
				
				int pk = entity.getId();
				DynamicUpdateEntity find = em.find(DynamicUpdateEntity.class, pk);
				
				find.setDefNotNull("change");
				em.flush();
				// @DynamicUpdate 적용 X
				/*
				 * update dynamic_update_entity
				 * set
				 * 	def_not_null='change', <- set문에 모든 필드가 포함됨
				 * 	def_nullable='init',
				 * 	nullable='init'
				 * where
				 * 	id=1;
				 */
				
				// @DynamicUpdate 적용 O
				/*
				 * update dynamic_update_entity
				 * set
				 * 	def_not_null='change' <- set문에 변경된 필드만 포함됨
				 * where
				 * 	id=1;
				 */
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

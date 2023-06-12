package study.hibernate.entitymanager.havenotforeignkey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.SeqEntity;

/*
 *	@Id 자동입력 엔티티 merge 테스트
 *
 *	persist시에는 ID미설정이지만, merge시에는 ID필드 설정 가능
 *	merge시 db에 없는 pk넣어주는 경우 pk는 다음 시퀀스 값으로 설정
 */
public class SeqEntityMergeTest {

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

				SeqEntity entity = new SeqEntity();
				// insert into seq_entity (id,name) values(1,null);
				em.persist(entity);

				entity.getId(); // 1

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

	// id 미설정 후 merge -> insert
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				SeqEntity entity = new SeqEntity();
				SeqEntity merged = em.merge(entity);

				merged.getId(); // 2

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

	// id 설정(DB 존재O) 후 merge -> update
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// DB에 존재O id설정
				SeqEntity entity = new SeqEntity(1, "change");
				SeqEntity merged = em.merge(entity);

				merged.getId(); // 1

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

	// id 설정(DB 존재X) 후 merge -> insert (예외 미발생. pk는 다음 시퀀스값으로 설정됨)
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// DB에 존재X id설정
				SeqEntity entity = new SeqEntity(10000, "change");
				// select pk 10000시 없음 -> call next value -> pk 2 엔티티 생성
				SeqEntity merged = em.merge(entity);

				merged.getId(); // 2

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

package study.hibernate.jpql;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Person;

/*
 * JPQL의 조회 대상은 엔티티, 임베디드 타입, 값 타입 같이 다양함
 * 
 * 현재 테스트: 엔티티 타입의 JPQL 대상으로 테스트
 */
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class JpqlPersistenceContextTest {

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

				em.persist(new Person(1, "김하나"));
				em.persist(new Person(2, "김하영"));
				em.persist(new Person(3, "김민수"));
				
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
	 * 엔티티 타입 프로젝션 조회 JPQL의 경우 쿼리 결과과 영속성 컨텍스트에 로드된다.
	 */
	@Test
	void entityTypeProjectionSelectJPQL() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				// 엔티티 타입의 프로젝션에 해당하는 조회 JPQL -> 영속성 컨텍스트에 올린다.
				TypedQuery<Person> selectQeury = em.createQuery("SELECT p FROM Person p WHERE p.name LIKE '김하%'", Person.class);
				List<Person> persons = selectQeury.getResultList();
				
				assertEquals(2, persons.size());
				
				log.debug("===> select 쿼리 안나가야함");
				em.find(Person.class, 1);
				
				log.debug("===> select 쿼리 안나가야함");
				em.find(Person.class, 2);
				
				log.debug("===> select 쿼리 나가야함");
				em.find(Person.class, 3);
			
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
	 * JPQL은 영속성 컨텍스트에 관계없이 DB에 바로 쿼리가 날라간다.
	 */
	@Test
	void jpql() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				// @Id=1 엔티티 영속성 컨텍스트 로드
				em.find(Person.class, 1);
				
				log.debug("===> 영속성 컨텍스트에 존재하지만 DB에 쿼리 무조건 날라감");
				TypedQuery<Person> selectQeury = em.createQuery("SELECT p FROM Person p WHERE p.id = 1", Person.class);
				Person person = selectQeury.getSingleResult();
				
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
	 * UPDATE JPQL 수행 후 최신 DB의 상태는 영속성 컨텍스트에 자동으로 반영되지 않음
	 */
	@Test
	void updateJpql() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				// @Id=1 엔티티 영속성 컨텍스트 로드
				Person find = em.find(Person.class, 1);
				
				Query updateQuery = em.createQuery("UPDATE Person p SET p.name = '김하나둘셋' WHERE p.id = 1");
				updateQuery.executeUpdate();
				
				// 최신 상태의 내용을 영속성 컨텍스트에 반영되지 않음.
				Person reFind = em.find(Person.class, 1);
				log.debug("===> refind: {}", reFind.toString());
				
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
	 * 엔티티 타입 프로젝션 조회 JPQL을 사용해도 영속성 컨텍스트에 대상 @Id의 엔티티가 존재했다면 DB에서 조회한 값은 버려지고 기존의 영속성 컨텍스트에 존재했던 것을 그대로 사용한다.
	 */
	@Test
	void selectJpqlTest() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				// 주석처리 유무(기존에 영속성 컨텍스트에 존재했나)에 따라 select jpql의 결과가 달라짐
				Person find = em.find(Person.class, 1);
				
				Query updateQuery = em.createQuery("UPDATE Person p SET p.name = '김하나둘셋' WHERE p.id = 1");
				updateQuery.executeUpdate();
				
				// DB의 최신 내용을 영속성 컨텍스트에 반영하려고 SELECT JPQL조회해도 영속성 컨텍스트에 @Id=1엔티티가 존재했으므로 업데이트되지 않음
				TypedQuery<Person> selectQuery = em.createQuery("SELECT p FROM Person p where p.id = 1", Person.class);
				List<Person> persons = selectQuery.getResultList();
				for (Person p : persons) {
					log.debug("{}", p.toString());
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

}

//다른 트랜잭션에서 작업
/*
new Thread() {

	@Override
	public void run() {
		EntityManager anotherEm = null;
		EntityTransaction anotherTx = null;
		try {
			anotherEm = emf.createEntityManager();
			anotherTx = anotherEm.getTransaction();
			anotherTx.begin();
		
			anotherTx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			anotherTx.rollback();
			throw new RuntimeException(e);
		} finally {
			anotherTx = null;
			anotherEm.close();
			anotherEm = null;
		}
	}

}.start();
*/
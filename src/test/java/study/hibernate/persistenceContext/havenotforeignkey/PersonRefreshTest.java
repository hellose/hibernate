package study.hibernate.persistenceContext.havenotforeignkey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;

/*
 * refresh(managed 상태 객체) -> managed 상태 객체의 모든 필드는 DB에서 가져온 값들로 모두 overwriting된다.
 * managed 상태의 변경을 무시하거나, JPQL 사용시 활용
 */
public class PersonRefreshTest {

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

				// pk 1 Person insert
				Person person1 = Person.builder().id(1).name("DB에 들어있던 사람").build();
				em.persist(person1);
				
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

	/*
	 * managed 상태 객체의 모든 필드는 DB에서 가져온 값들로 모두 overwriting된다. 또한 스냅샷이 갱신된다.
	 */
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);
				
				findPerson.setName("이름 변경");
				
				System.out.println("===> refresh person");
				em.refresh(findPerson);
				
				// 모든 필드가 overwriting되어 find 시점의 값으로 돌아감
				System.out.println("findPerson.getName(): " + findPerson.getName());
				
				System.out.println("===> commit person");
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
	 * JPQL 사용시 refresh를 사용하여 최신 DB 값으로 갱신
	 */
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);
				System.out.println(findPerson.toString());
				
				// JPQL
				System.out.println("===> update JPQL");
				em.createQuery("update Person p set p.name = :name where p.id = :id")
					.setParameter("id", 1)
					.setParameter("name", "jpql로 이름 변경")
					.executeUpdate();
				System.out.println(findPerson.toString());

				System.out.println("===> refresh person");
				em.refresh(findPerson);
				System.out.println(findPerson.toString());
				
				System.out.println("===> commit person");
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

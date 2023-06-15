package study.hibernate.entitymanager.havenotforeignkey;

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

/*
 * refresh(managed 상태 객체) -> managed 상태 객체의 모든 필드는 DB에서 가져온 값들로 모두 overwriting된다.
 * managed 상태의 변경을 무시하거나, JPQL 사용시 활용
 */
public class PersonRefreshTest {

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
				tx = null;
				em = null;
				emf = null;
				System.out.println("===> beforeEach");
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
	
	/*
	 * DB에서 다시 로딩시키는 refresh를 사용하기 위해서는 flush가 되어 DB에 엔티티가 존재하는 상태여야한다.
	 */
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person2 = Person.builder().id(2).name("person2").build();
				em.persist(person2);
				
				//insert쿼리가 flush되지 않은 상태에서 refresh호출시 바로 select쿼리가 나가 예외발생함
//				em.flush(); //주석해제시 예외발생안함
				em.refresh(person2);
				
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

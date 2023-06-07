package study.hibernate.entitymanager.havenotforeignkey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Person;

/*
 * remove(managed 상태 객체) -> removed 상태 객체로 변경됨 + 영속성 컨텍스트에서 없어짐
 * IllegalArgumentException if the instance is not an entity or is a detached entity
 */
public class PersonRemoveTest {

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

				// pk 1000 Person insert
				Person person1000 = Person.builder().id(1000).name("DB에 들어있던 사람").build();
				em.persist(person1000);

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
	 * managed 상태의 객체를 remove
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

				// managed 객체에 remove를 호출하면 delete스케줄링이 걸리고 flush시점에서 delete쿼리가 발생
				System.out.println("===> remove person");
				em.remove(findPerson);

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
	 * remove된 후 Removed 엔티티 상태 확인 테스트
	 * 
	 * 1. remove된 엔티티는 영속성 컨텍스트에 존재하지 않는다 
	 * 2. remove된 엔티티는 flush시점에 해당 row가 delete예정이기 때문에 find를 호출해도 DB에서 select하는 쿼리가 나가지 않는다.
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

				System.out.println("===> remove person");
				em.remove(findPerson);

				// 영속성 컨텍스트에 존재하지 않음
				System.out.println("em.contains(findPerson): " + em.contains(findPerson)); //false

				// delete 스케줄링이 걸린 엔티티라서 flush시점에 delete될 객체이기 때문에 find시 select쿼리가 나가지 않음
				System.out.println("===> find person");
				Person refindPerson = em.find(Person.class, 1);
				System.out.println("refindPerson == null: " + (refindPerson == null)); // true

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
	 * removed 상태의 객체의 필드를 변경하고 다시 persist할 경우 dirty checking이 동작한다.
	 */
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);
				
				
				System.out.println("===> remove person");
				em.remove(findPerson);
				
				// remove 호출 후 removed 상태 객체의 필드 변경
				findPerson.setName("변경");
				
				// removed 상태 객체 persist
				System.out.println("===> persist person");
				em.persist(findPerson);
				
				// update 쿼리 나감 <- remove시 영속성 컨텍스트에서 제거되지만 스냅샷 정보는 보관중인 상태임을 확인 가능
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
	 * remove 후 removed 상태 엔티티가 아닌 teansient 상태 엔티티를 persist하는 경우
	 */
	@Test
	void test4() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);
				
				System.out.println("===> remove person");
				em.remove(findPerson);
				
				// transient 상태 객체 persist
				Person transientPerson = Person.builder().id(1).name("transient 상태 엔티티").build();
				System.out.println("===> persist person");
				em.persist(transientPerson); // remove 호출시 delete 스케줄링이 걸려있던 쿼리가 여기서 나감
				
				System.out.println("===> commit");
				tx.commit(); // persist 호출시 만들어진 insert 쿼리가 여기서(flush)나감
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
	 * remove -> flush가 추가됨 -> persist
	 */
	@Test
	void test5() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);
				
				System.out.println("===> remove person");
				em.remove(findPerson);
				
				System.out.println("===> flush person");
				em.flush();
				System.out.println("em.contains(findPerson): " + em.contains(findPerson));
				
				System.out.println("===> persist person");
				em.persist(findPerson); // ok
				
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
	 * persist 후 remove하는 경우
	 */
	@Test
	void test6() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				System.out.println("===> persist person");
				Person person = Person.builder().id(2).name(null).build();
				em.persist(person); //insert 쿼리 생성
				
				System.out.println("===> remove person");
				em.remove(person); //delete 쿼리 생성
				
				//insert 쿼리
				//delete 쿼리
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

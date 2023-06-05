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
 * persist(managed 상태 객체) -> 아무런 일도 일어나지 않는다.
 * persist(new/transient 상태 객체) -> managed 상태 객체가 되며, 영속성 컨텍스트에 저장된다.
 */
public class PersonPersistTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	/*
	 * 모든 테스트 수행 전 insert
	 */
	@BeforeEach
	void init() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person = Person.builder().id(1000).name("DB에 들어있던 사람임").build();
				em.persist(person);

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
	 * persist 를 통해 영속성 컨텍스트에 저장되고 managed 상태가 된다 -> flush 시점에 해당 insert쿼리가 발생한다.
	 */
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// transient 상태
				Person person = Person.builder().id(1).name("1").build(); // 비영속 상태 person

				// managed 상태 + 영속성 컨텍스트에 저장
				System.out.println("===> persist person");
				em.persist(person);

				System.out.println("===> commit");
				// flush
				tx.commit(); // pk 1 person insert
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
	 * 엔티티 @Id전략이 자동이 아닌 수동 셋팅일때 @Id에 해당하는 필드를 셋팅하지 않고 persist시 에러 발생
	 */
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// org.hibernate.id.IdentifierGenerationException: ids for this class must be manually assigned before calling save()
				Person person = Person.builder().name("1").build();
				System.out.println("===> persist person");
				em.persist(person); //

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
	 * persist시점에 managed 상태가 되면서 해당 엔티티의 모든 필드들이 캡처링하여 스냅샷으로 기억하고 있음 
	 * dirty checking = persist시점의 스냅샷과 flush 시점에서의 필드 상태를 비교하여 update 쿼리가 만들어짐
	 */
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person = Person.builder().id(1).name("1").build();
				System.out.println("===> persist person");
				// 스냅샷에 대하여 insert 쿼리 생성
				em.persist(person);

				// 필드 변경
				person.setName("변경1");
				person.setName("변경2");
				
				// flush 시점에서 persist의 스냅샷과 비교하여 다른 필드만 update 쿼리 생성
				// 중간에 naem필드는 두번 바뀌었지만 flush시점에 한번만 비교하기 때문에 update 쿼리는 한번만 나감
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
	 * 영속성 컨텍스트에서 특정 identifier에 대한 managed 상태 엔티티는 하나 이상 존재할 수 없다.
	 */
	@Test
	void test4() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person = Person.builder().id(1).name("1").build();
				System.out.println("===> persist person");
				em.persist(person);

				Person test = Person.builder().id(1).name("1").build();
				// 영속성 컨텍스트에 pk 1인 Person엔티티는 person이 존재하므로 에러 발생
				// javax.persistence.EntityExistsException <- 동일한 identifier에 대한 엔티티가 영속성 컨텍스트에 이미 존재
				System.out.println("===> persist person");
				em.persist(test);

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
	 * 영속성 컨텍스트에 특정 identifier에 대한 managed 엔티티가 존재하는 경우 find시 DB에서 select하지 않고 영속성 컨텍스트에 존재하는 객체를 사용한다.
	 */
	@Test
	void test5() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person = Person.builder().id(1).name("1").build();
				System.out.println("===> persist person");
				em.persist(person);
				System.out.println("em.contains(person) : " + em.contains(person));

				// identifier 1에 대해 기존 영속성 컨텍스트에서 managed 상태에 있는 객체 반환
				System.out.println("===> find person");
				Person findPerson = em.find(Person.class, 1);

				System.out.println("person == findPerson: " + (person == findPerson));

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
	 * persist를 통해 생성된 insert 쿼리는 실제 flush시점에 날라가 에러가 발생하는 것을 주의한다.
	 */
	@Test
	void test6() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// persist 호출시 체크하는 사항은 1000번 identifier에 대한 managed 객체가 영속성 컨텍스트에 존재하지 않기만 하면 ok임
				Person person = Person.builder().id(1000).name("DB에 존재하는 1000번").build();
				System.out.println("===> persist person");
				em.persist(person);

				// 실제 DB에 insert 쿼리가 수행 후 에러가 발생
				// org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: Unique index or primary key violation
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
	 * 이미 영속 상태인 객체에 persist를 날려봐야 아무런 일도 일어나지 않는다.
	 */
	@Test
	void test7() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Person person = Person.builder().id(1).name("1").build();
				System.out.println("===> persist person");
				em.persist(person);
				// 이미 영속 상태인 객체에 persist를 아무리 호출해봐야 아무런 일도 일어나지 않는다.
				em.persist(person);
				em.persist(person);

				person.setName("변경1");
				em.persist(person);
				em.persist(person);

				person.setName("변경2");
				em.persist(person);
				em.persist(person);
				em.persist(person);
				em.persist(person);
				em.persist(person);
				
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

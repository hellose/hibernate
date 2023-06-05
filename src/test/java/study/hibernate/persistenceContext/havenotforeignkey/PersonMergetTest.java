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
 * merge(new/transient 상태 객체)
 * 
 * 영속성 컨텍스트에 new/transient 상태 객체 id에 해당하는 managed 객체가 없음
 * 		-> DB에서 select로 로드
 * 			-> 없는 경우(Case1) -> new/transient 상태 객체의 모든 값이 복사되고 insert 쿼리 생성 -> 새로 만들어낸 managed 객체 리턴 (새로운 객체를 리턴한다는 점에서 persist와는 엄연히 다름)
 * 			-> 존재하는 경우(Case2) -> new/transient 상태 객체와 로드된 객체 비교 후 로드된 객체와 다른 값들만 update 쿼리 생성됨 -> 생성된 managed 객체 리턴
 * 
 * 영속성 컨텍스트에 new/transient 상태 객체 id에 해당하는 managed 객체가 존재(Case3)
 * 		-> new/transient 상태 객체의 모든 필드값을 managed 상태 객체에 overwriting한 후 기존 managed 상태 객체 리턴  
 */
public class PersonMergetTest {

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
	 * Case1
	 */
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// new/transient 상태 객체
				Person transientPerson = Person.builder().id(2).name("transient 객체에 설정된 이름").build();
				System.out.println("===> merge person");
				// insert 쿼리 생성
				// 모든 필드 값이 복사된 새로운 managed 객체 리턴
				Person mergedPerson = em.merge(transientPerson);
				System.out.println("mergedPerson == transientPerson: " + (mergedPerson == transientPerson)); // false
				System.out.println("em.contains(mergedPerson): " + em.contains(mergedPerson)); // true
				
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
	 * Case2
	 */
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// new/transient 상태 객체
				Person transientPerson = Person.builder().id(1).name("transient 객체에 설정된 이름").build();
				System.out.println("===> merge person");
				// update 쿼리
				Person mergedPerson = em.merge(transientPerson);
				System.out.println("mergedPerson == transientPerson: " + (mergedPerson == transientPerson)); // false
				System.out.println("em.contains(mergedPerson): " + em.contains(mergedPerson)); // true
				
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
	 * Case3
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
				// 영속성 컨텍스트에 pk 1인 managed 상태 객체 존재 (객체A)


				// new/transient 상태 객체 (객체B)
				Person transientPerson = Person.builder().id(1).name("transient 객체에 설정된 이름").build();
				System.out.println("===> merge person");
				// 객체A 리턴
				// 객체A, 객체B 비교시 값이 달랐던 필드들만 포함된 update 쿼리 생성
				Person mergedPerson = em.merge(transientPerson);
				System.out.println("mergedPerson: " + mergedPerson);
				System.out.println("mergedPerson == findPerson: " + (mergedPerson == findPerson)); // true
				
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
	 * persist -> merge
	 */
	@Test
	void test4() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				//(객체A)
				Person persistedPerson = Person.builder().id(2).name("persist 객체에 설정된 이름").build();
				System.out.println("===> persist person");
				//스냅샷 기록 -> insert 쿼리 생성
				em.persist(persistedPerson);
				
				// 영속성 컨텍스트에 identifier가 1인 managed 상태 객체 존재 (객체A)
				
				// new/transient 상태 객체 (객체B)
				Person transientPerson = Person.builder().id(2).name("transient 객체에 설정된 이름").build();
				System.out.println("===> merge person");
				// 객체A,객체B 비교하여 스냅샷 기록 -> update 쿼리 생성
				Person mergedPerson = em.merge(transientPerson);
				System.out.println("mergedPerson: " + mergedPerson);
				System.out.println("mergedPerson == persistedPerson: " + (mergedPerson == persistedPerson)); // true
				
				//insert
				//update
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

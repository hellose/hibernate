package study.hibernate.entitymanager.havenotforeignkey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Person;

/*
 * Hibernate 프록시 동작.
 */
public class PersonProxyTest {

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

				// pk 1,2 Person insert
				Person person1 = Person.builder().id(1).name("person1").build();
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
	 * 프록시 객체와 내부 엔티티 객체 로딩
	 */
	@Test
	void test1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// Hibernate가 제공해주는 Person클래스를 상속한 클래스 인스턴스(프록시 객체)리턴
				System.out.println("===> getReference person");

				Person refPerson = em.getReference(Person.class, 1); // select 쿼리 나가지 않음
				System.out.println("refPerson: " + refPerson.getClass().getSimpleName()); // Person$HibernateProxy$MZY8KWn8

				// 최초 필드 접근시 -> select 쿼리가 나가고 프록시 객체의 내부 필드에 엔티티 객체가 초기화됨 -> 로드된 엔티티의 값 가져옴
				System.out.println("===> refPerson.getName()");
				refPerson.getName(); // select 쿼리 나감

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
	 * 프록시 객체의 내부 엔티티 객체 초기화되었는지 확인하는법 
	 * -> PersistenceUnitUtil.isLoaded (JPA표준 O), Hibernate.isLoad (Hibernate 제공)
	 */
	@Test
	void test2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				PersistenceUnitUtil puu = emf.getPersistenceUnitUtil();

				System.out.println("===> getReference person");
				Person refPerson = em.getReference(Person.class, 1);

				System.out.println("===> isLoaded: " + puu.isLoaded(refPerson)); // false
				// Hibernate.isInitialized(refPerson);

				// 필드 접근
				System.out.println("===> refPerson.getName()");
				refPerson.getName();

				System.out.println("===> isLoaded: " + puu.isLoaded(refPerson)); // true
				// Hibernate.isInitialized(refPerson);

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
	 * 프록시 객체의 내부 엔티티 객체 강제 초기화 -> Hibernate.initialize (JPA표준 X, Hibernate제공)
	 */
	@Test
	void test3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				PersistenceUnitUtil puu = emf.getPersistenceUnitUtil();

				System.out.println("===> getReference person");
				Person refPerson = em.getReference(Person.class, 1);

				System.out.println("===> isLoaded: " + puu.isLoaded(refPerson)); // false

				// 프록시 객체 내부 엔티티 강제 초기화
				System.out.println("===> 프록시 객체 내부 엔티티 강제 초기화");
				Hibernate.initialize(refPerson);

				System.out.println("===> isLoaded: " + puu.isLoaded(refPerson)); // true

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
	 * 특정 Id에 해당하는 엔티티 조회시 동일 타입 인스턴스 보장
	 * Case1. find -> getReference -> (find시 로딩된 Person객체 리턴) 
	 * Case2. getReference -> find (getReference시 로딩된 프록시 객체 내부 Person필드 초기화(select쿼리) 후 프록시 객체 리턴)
	 */
	@Test
	void test4() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				PersistenceUnitUtil puu = emf.getPersistenceUnitUtil();
				
				// Case1: find -> getReference
				Person p1 = em.find(Person.class, 1);
				Person ref1 = em.getReference(Person.class, 1);
				System.out.println("class: " + ref1.getClass().getSimpleName()); // Person
				System.out.println("동일: " + (p1 == ref1)); // true
				System.out.println("================> clear");
				em.clear();
				
				// Case2: getReference -> find
				Person ref2 = em.getReference(Person.class, 1);
				System.out.println("contains: " + em.contains(ref2)); // true
				System.out.println("loaded: " + puu.isLoaded(ref2)); // false
				System.out.println("================> clear");
				em.clear();

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

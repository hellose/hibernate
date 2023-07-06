package study.hibernate.relationship;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.relationship.case4.Locker;
import study.hibernate.relationship.case4.Member;

/*
 * 주테이블 Member에 fk가 존재하는 일(주인)대일 - 단방향
 */

/*
Hibernate: 
    
    create table locker (
       id integer not null,
        primary key (id)
    )
Hibernate: 
    
    create table member (
       id integer not null,
        locker_id integer,
        primary key (id)
    )
Hibernate: 
    
    alter table member 
       add constraint FKtnhmjyc273qn6jvvlng5e0mic 
       foreign key (locker_id) 
       references locker
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Case4Test {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	@AfterEach
	void afterEach() {
		tx = null;
		em = null;
		emf = null;
	}

	@Test
	@Order(1)
	void initTable() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				emf.close();
			}
		});
	}

	@Test
	@Order(2)
	void insertData() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Locker locker1 = new Locker();
				em.persist(locker1);

				Locker locker2 = new Locker();
				em.persist(locker2);

				Member mem1 = new Member();
				mem1.setLocker(locker1);
				em.persist(mem1);

				Member mem2 = new Member();
				mem2.setLocker(locker1);
				em.persist(mem2);

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

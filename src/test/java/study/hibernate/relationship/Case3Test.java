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
import study.hibernate.relationship.case3.Member;
import study.hibernate.relationship.case3.Team;

/*
 * 일(주인)대다 - 단방향.    (일대다 양방향은 존재하지 않음)
 */

/*
Hibernate: 
    
    create table member (
       id integer not null,
        team_id integer,
        primary key (id)
    )
Hibernate: 
    
    create table team (
       id integer not null,
        primary key (id)
    )
Hibernate: 
    
    alter table member 
       add constraint FKcjte2jn9pvo9ud2hyfgwcja0k 
       foreign key (team_id) 
       references team
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Case3Test {

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

				Member mem1 = new Member();
				em.persist(mem1);
				Member mem2 = new Member();
				em.persist(mem2);
				Member mem3 = new Member();
				em.persist(mem3);

				Team team1 = new Team();
				em.persist(team1);

				Team team2 = new Team();
				team2.getMembers().add(mem1);
				team2.getMembers().add(mem2);
				em.persist(team2);

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

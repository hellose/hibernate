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

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.relationship.abnormalcase1.Member;
import study.hibernate.relationship.abnormalcase1.Team;

/*
 * Member.team(@OneToOne) - Team.member(@OneToMany) 인 경우 테스트
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
@Slf4j
public class Abnormalcase1Test {

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

				Team team1 = new Team();
				em.persist(team1);
				
				Team team2 = new Team();
				em.persist(team2);
				
				Member mem1 = new Member();
				mem1.setTeam(team1);
				em.persist(mem1);
				
				Member mem2 = new Member();
				mem2.setTeam(team1);
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
	
	@Test
	@Order(3)
	void findEntity() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				Member findMem = em.find(Member.class, 3);
				findMem.getTeam().getMembers().size();
				
				log.debug("===> clear");
				em.clear();
				
				Team findTeam = em.find(Team.class, 1);
				log.debug("size: {}", findTeam.getMembers().size());
				
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

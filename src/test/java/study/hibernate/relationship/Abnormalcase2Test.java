package study.hibernate.relationship;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.relationship.abnormalcase2.Member;
import study.hibernate.relationship.abnormalcase2.Team;

/*
 * Member.team(@ManyToOne) - Team.member(@OneToOne) 테스트
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
public class Abnormalcase2Test {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	private int noMemberTeamId;
	private int oneMemberTeamId;
	private int twoMemberTeamId;

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

				Team noMemberTeam = new Team();
				em.persist(noMemberTeam);
				this.noMemberTeamId = noMemberTeam.getId();

				Team oneMemberTeam = new Team();
				em.persist(oneMemberTeam);
				this.oneMemberTeamId = oneMemberTeam.getId();

				Team twoMemberTeam = new Team();
				em.persist(twoMemberTeam);
				this.twoMemberTeamId = twoMemberTeam.getId();

				Member mem1 = new Member();
				mem1.setTeam(oneMemberTeam);
				em.persist(mem1);

				Member mem2 = new Member();
				mem2.setTeam(twoMemberTeam);
				em.persist(mem2);

				Member mem3 = new Member();
				mem3.setTeam(twoMemberTeam);
				em.persist(mem3);

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
	@DisplayName("소속된 멤버가 없는 팀 조회 -> 팀 멤버 조회")
	void findTest1() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Team team = em.find(Team.class, noMemberTeamId);
				Member m = team.getMember();
				assertNull(m);

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
	@Order(4)
	@DisplayName("소속된 멤버가 한명인 팀 조회 -> 팀 멤버 조회")
	void findTest2() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				Team team = em.find(Team.class, oneMemberTeamId);
				Member m = team.getMember();
				assertNotNull(m);

				log.debug("===> id: {}", m.getId());

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
	@Order(5)
	@DisplayName("소속된 멤버가 두명인 팀 조회 -> 팀 멤버 조회")
	void findTest3() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				// 팀에 소속된 멤버가 한명이 아니기 때문에 예외 발생
				Team team = em.find(Team.class, twoMemberTeamId);

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

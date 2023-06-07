package study.hibernate.entitymanager.haveforeignkey;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Member;
import study.hibernate.entity.Team;

/*
 * EntityManager.refresh(영속 객체)
 */
//TODO -보완
public class RefreshTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	// 테스트 데이터 insert
	@Test
	void insertEntity() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// insert into team (id,name) values(1,'team1');
			Team team = Team.builder().id(1).name("team1").build();
			em.persist(team);

			// insert into member (id,name,team_id) values(1,'mem1',1);
			Member mem = Member.builder().id(1).name("mem1").team(team).build();
			em.persist(mem);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}

	// 1. Member 조회 -> 2. 영속 Member 필드 변경 -> 3. 영속 Member refresh -> 4. commit
	// 결과: refresh에 의해 dirty checking 무시됨
	@Test
	void test1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 1. Member 조회
			Member findMem = em.find(Member.class, 1);
			System.out.println("===> " + findMem.toString());
			System.out.println("===> " + findMem.getTeam().toString());

			// 2. 영속 Member 필드 변경
			findMem.setName("변경");

			// 3. 영속 Member refresh
			em.refresh(findMem);
			System.out.println("===> " + findMem.toString());
			System.out.println("===> " + findMem.getTeam().toString());

			// 4. commit
			tx.commit(); //refresh에 의해 update 쿼리 안나감
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}

	// 1. Member 조회 -> 2. 영속 Member 필드 변경 -> 3. 영속 Member refresh -> 4. 영속 Member 필드 변경 -> 5. commit
	// 결과: commit 호출시 update 쿼리 발생함
	@Test
	void test2() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 1. Member 조회
			Member findMem = em.find(Member.class, 1);
			System.out.println("===> " + findMem.toString());
			System.out.println("===> " + findMem.getTeam().toString());
			
			// 2. 영속 Member 필드 변경
			findMem.setName("변경");

			// 3. 영속 Member refresh
			em.refresh(findMem);
			System.out.println("===> " + findMem.toString());
			System.out.println("===> " + findMem.getTeam().toString());
			
			// 4. 영속 Member 필드 변경
			findMem.setName("변경");
			
			tx.commit(); //update 쿼리 발생함
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}

	// 1. 영속 Member 조회 -> 2. Member, Team jpql -> 3. 영속 Member refresh -> 4. commit
	// 결과: team필드 @ManyToOne(cascade = CascadeType.REFRESH) 유무에 의해 team의 동기화 유무가 달라짐
	@Test
	void test3() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 1. 영속 Member 조회
			Member findMem = em.find(Member.class, 1);
			System.out.println("===> " + findMem.toString());
			System.out.println("===> " + findMem.getTeam().toString());

			// 2. Member, Team jpql
			em.createQuery("update Member m set m.name = :name where m.id = :id")
								.setParameter("id", 1)
								.setParameter("name", "변경")
								.executeUpdate();
			
			em.createQuery("update Team t set t.name = :name where t.id = :id")
								.setParameter("id", 1)
								.setParameter("name", "변경")
								.executeUpdate();
			
			// 값 확인
			// old data
			System.out.println("===> " + findMem.toString());
			// old data
			System.out.println("===> " + findMem.getTeam().toString());
			
			// 3. 영속 Member refresh
			em.refresh(findMem);

			// 값 확인
			// refreshed data
			System.out.println("===> " + findMem.toString());
			// @ManyToOne(cascade = CascadeType.REFRESH) 유무에 의해 team의 동기화 유무가 달라짐
			System.out.println("===> " + findMem.getTeam().toString());

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}

}

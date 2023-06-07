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
 * EntityManager.merge(비영속 객체)
 * : upsert와 동일한 기능을함
 */
//TODO -보완
public class MergeTest {

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
			// insert into team (id,name) values(2,'team2');
			Team team = Team.builder().id(1).name("team1").build();
			em.persist(team);
			Team team2 = Team.builder().id(2).name("team2").build();
			em.persist(team2);

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

	// 1. fk를 null로 셋팅한 비영속 Member merge -> 2. commit
	@Test
	void test1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			// fk(team필드)를 null로 셋팅한 비영속 Member 셋팅
			Member dataMem = Member.builder().id(1000).name("mem1000").build();
			
			System.out.println("===> merge");
			
			// 1. fk를 null로 셋팅한 비영속 Member merge
			// 영속성컨텍스트에 1000번 Member 조회 결과 없음 -> db에서 select 결과 없음 -> persist같이 동작 -> 영속성 컨텍스트에 pk1 Member 보관
			// team필드 셋팅하지 않았으므로 Team이 db에 존재하는지 체크하는 select쿼리가 나갈 필요가 없기 때문에 쿼리 안나감
			Member afterMerged = em.merge(dataMem);
			
			System.out.println("em.contains(afterMerged): " + em.contains(afterMerged)); //true
			System.out.println("em.contains(dataMem): " + em.contains(dataMem)); //false
			
			// 2. commit
			System.out.println("===> commit");
			// insert 쿼리나감 (dataMem 빌드시 team이 null이였기 때문에 team_id는 null로 들어감)
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	// 1. Member 조회 -> 2. 비영속 Member merge -> 3. commit
	@Test
	void test2() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			// 1. Member 조회
			System.out.println("===> find");
			Member findMem = em.find(Member.class, 1);
			printMember(findMem);
			
			// 2. 비영속 Member merge
			Member dataMem = Member.builder().id(1).name("변경").build(); // team필드 null인 상태임!!!!
			
			System.out.println("===> merge");
			//team 필드가 null이므로 fk 체크가 필요없음 -> select team 쿼리 필요없음
			Member afterMerged = em.merge(dataMem);
			
			System.out.println("findMem == afterMerged: " + (findMem == afterMerged));
			printMember(findMem);
			
			// 3. commit
			// team필드 null이므로 update member시 fk컬럼 team_id는 null로 들어감
			System.out.println("===> commit");
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	// test2()와 동일. fk에 비영속 Team pk2을 넣어 Merge하는 경우
	@Test
	void test3() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			// 1. Member 조회
			System.out.println("===> find");
			// 영속성 컨텍스트에 pk1인 Member를 보관하면서 pk1 Team 엔티티도 같이 보관됨(Eager라서)
			Member findMem = em.find(Member.class, 1);
			printMember(findMem);
			
			// 2. 비영속 Member merge
			Member dataMem = Member.builder()
									.id(1)
									.name("변경")
									.team(Team.builder().id(2).build()) // 영속성 컨텍스트에 없는 pk 2인 Team셋팅 해보면
									.build(); 
			
			System.out.println("===> merge");
			Member afterMerged = em.merge(dataMem);
			
			System.out.println("findMem == afterMerged: " + (findMem == afterMerged));
			printMember(findMem);
			
			// 2. commit
			System.out.println("===> commit");
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	static void printMember(Member mem) {
		System.out.println(mem.toString());
		if (mem.getTeam() != null) {
			System.out.println(mem.getTeam().toString());
		}else {
			System.out.println(mem.getTeam());
		}
	}

}

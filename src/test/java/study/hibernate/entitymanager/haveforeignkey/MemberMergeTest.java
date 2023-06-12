package study.hibernate.entitymanager.haveforeignkey;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Member;
import study.hibernate.entity.Team;

/**
 * fk를 엔티티를 가지는 엔티티 merge 테스트
 */
public class MemberMergeTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	/**
	 * insert into team(id,name) values(1,'team1');<br>
	 * insert into team(id,name) values(2,'team2');<br>
	 * insert into member(id,name,team_id) valuse(1,'mem1',1);<br>
	 */
	@BeforeEach
	void beforeEach() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Team team = Team.builder().id(1).name("team1").build();
			em.persist(team);
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

	/**
	 * fk=null인 Member merge
	 */
	@Test
	void test1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// fk(team필드)를 null로 셋팅한 transient Member
			Member dataMem = Member.builder().id(1000).name("mem1000").build();

			System.out.println("===> merge");
			// 영속성 컨텍스트에 없는 경우 DB에서 조회 -> 존재x -> transient객체의 모든 필드를 복사한 managed객체 리턴
			Member afterMerged = em.merge(dataMem);

			System.out.println("em.contains(afterMerged): " + em.contains(afterMerged)); // true
			System.out.println("em.contains(dataMem): " + em.contains(dataMem)); // false

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

	/**
	 * 영속성 컨텍스트에 fk Team객체가 없는 상태에서 fk=transient인 Member merge
	 */
	@Test
	void test2() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			System.out.println("===> merge Member 2");
			Team transientTeam1 = Team.builder().id(1).build();
			Member dataMem2 = Member.builder().id(2).name("mem2").team(transientTeam1).build();
			// 영속성 컨텍스트 pk 2 member 미존재
			// -> db pk 2 member 조회 미존재
			// -> insert member 쿼리가 생성될 것임
			// -> 기본 필드 overwriting
			// -> transient상태의 transientTeam1이 유효한지 fk체크 select쿼리 발생
			// -> 유효
			// -> pk 1 Team을 영속성 컨텍스트에 올림
			// -> 리턴될 managed객체 Member의 필드로 영속성 컨텍스트에 올라간 Team 셋팅
			Member afterMerged = em.merge(dataMem2);

			// managed Team으로 변경됨
			System.out.println("afterMerged.getTeam() == transientTeam1: " + (afterMerged.getTeam() == transientTeam1)); // false
			System.out.println("em.contains(afterMerged.getTeam()): " + em.contains(afterMerged.getTeam())); // true

			// 이미 존재하므로 select 쿼리 안나감
			System.out.println("===> find Team 1");
			em.find(Team.class, 1);

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

	/**
	 * 영속성 컨텍스트에 fk Team객체가 있는 경우 fk=transient인 Member merge
	 */
	@Test
	void test3() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 영속성 컨텍스트에 미리 올려놓음
			System.out.println("===> find Team 1");
			Team findTeam1 = em.find(Team.class, 1);

			Team transientTeam1 = Team.builder().id(1).build();
			Member dataMem = Member.builder().id(2).name("mem2").team(transientTeam1).build();
			System.out.println("===> merge Member 2");
			// transientTeam1의 pk 1이 유효한지(fk 1)체크할 때 영속성 컨텍스트에 존재하므로 select워리 안나감
			Member afterMerged = em.merge(dataMem);

			// managed Member의 필드로 기존 영속성 컨텍스트에 존재했던 Team 사용
			System.out.println("afterMerged.getTeam() == findTeam1: " + (afterMerged.getTeam() == findTeam1)); // true

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

	/**
	 * fk=db에 존재하지않는 transient인 Member merge시 merge에서 예외 발생
	 */
	@Test
	void test4() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Team noDbTransientTeam2 = Team.builder().id(2).build();
			Member dataMem = Member.builder().id(2).name("mem2").build();
			dataMem.setTeam(noDbTransientTeam2);

			System.out.println("===> merge");
			// noDbTransientTeam2의 id 2에 해당하는 Team을 조회만 하지 않고 영속성 컨텍스트에 올리려하기 때문에 예외 발생
			// EntityNotFoundException
			Member afterMerged = em.merge(dataMem);

			// 가능? 이전에 예외발생
			System.out.println("===> setTeam(managed Team)");
			afterMerged.setTeam(em.find(Team.class, 1));
			
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
	
	/**
	 * '@'ManyToOne(cascade = CascadeType.MERGE)<br>
	 * '@'JoinColumn(name = "team_id")<br>
	 * private Team team;<br>
	 * <br>
	 * fk가 managed인 Team인 Member merge
	 */
	/*
	 * @ManyToOne(cascade = CascadeType.MERGE) 설정
	 */
	@Test
	void test5() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			Team findTeam1 = em.find(Team.class, 1);
			Member dataMem = Member.builder().id(2).name("mem2").build();
			dataMem.setTeam(findTeam1);
			
			System.out.println("===> merge");
			Member afterMerged = em.merge(dataMem);
			
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
	
}

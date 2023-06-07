package study.hibernate.entitymanager.haveforeignkey;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entitymanager.haveforeignkey.Member;
import study.hibernate.entitymanager.haveforeignkey.Team;

/**
 * Member persist시 fk team필드 확인 위주의 테스트 진행 (각 테스트에서 명시하지 않는한 @ManyToOne이 기본)<br>
 * 1. fk필드=null인 케이스<br>
 * 2. fk필드=not null, team필드 transient 상태 Team 인 경우<br>
 * 3. fk필드=not null, team필드 managed 상태 Team 인 경우
 */
public class MemberPersistTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	/**
	 * BeforeEach로 pk 1 Team persist
	 */
	@BeforeEach
	void init() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// pk 1 Team insert
			Team team = Team.builder().id(1).name("team1").build();
			em.persist(team);

			System.out.println("===> commit");
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
			System.out.println("===> BeforeEach End");
		}
	}
	
	/**
	 * fk=null<br>
	 * fk체크 쿼리 나갈필요가 없기 때문에 나가지 않는다.
	 */
	@Test
	void memberPersistWithNullFk() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// pk체크 쿼리 나가지 않음
			Member mem = Member.builder().id(1).name("mem1").team(null).build();
			System.out.println("===> persist mem");
			em.persist(mem);

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
	 * fk=notnull<br>
	 * 영속성 컨텍스트에 Team 존재 유무에 관계없이 fk에 transient 객체를 전달후 persist<br>
	 * 1. fk체크 쿼리는 항상 나감<br>
	 * 2. fk체크 쿼리결과 Team엔티티를 영속성 컨텍스트에 로드하지 않는다.
	 */
	@Test
	void fkCheckQueryAlwaysSendedWhenSettingTransientFk() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 1. 영속성 컨텍스트에 Team 미존재

			Member test1 = Member.builder().id(1).build();
			Team team1 = Team.builder().id(1).build(); // 1-1. DB에 pk존재하는 경우
			test1.setTeam(team1);
			System.out.println("===> persist mem");
			em.persist(test1);// fk체크 쿼리 나감.
			System.out.println("===> clear");
			em.clear();

			Member test2 = Member.builder().id(1).build();
			Team team2 = Team.builder().id(2).build(); // 1-2. DB에 pk존재하지 않는 경우
			test2.setTeam(team2);
			System.out.println("===> persist mem");
			em.persist(test2);// fk체크 쿼리 나감.
			System.out.println("===> clear");
			em.clear();

			// 2. 영속성 컨텍스트에 Team 존재

			em.find(Team.class, 1); // 영속성 컨텍스트에 존재
			Member test3 = Member.builder().id(1).build();
			Team team3 = Team.builder().id(1).build(); // 2-1. DB에 pk존재하는 경우
			test3.setTeam(team3);
			System.out.println("===> persist mem");
			em.persist(test3); // fk체크 쿼리 나감.
			System.out.println("===> clear");
			em.clear();

			Team persist = Team.builder().id(2).build();
			em.persist(persist); // 영속성 컨텍스트에 존재
			Member test4 = Member.builder().id(1).build();
			Team team4 = Team.builder().id(2).build();
			test4.setTeam(team4);
			System.out.println("===> persist mem");
			em.persist(test3); // fk체크 쿼리 나감.
			System.out.println("===> clear");
			em.clear();

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
	 * fk=notnull<br>
	 * 영속성 컨텍스트에 Team존재 유무와 관계없이 fk에 transient 객체 설정 후 persist<br>
	 * 1. fk필드는 persist전/후 동일한 transient 객체 유지
	 */
	@Test
	void keepTransientInstanceAfterPersist() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// 1. 영속성 컨텍스트에 Team 미존재
			
			Member test1 = Member.builder().id(1).build();
			Team team1 = Team.builder().id(1).build();
			test1.setTeam(team1);
			System.out.println("===> persist mem");
			em.persist(test1);
			System.out.println("fk필드 객체 persist전/persist후 동일한 transient 객체: " + (test1.getTeam() == team1));
			System.out.println("fk필드 객체는 managed 상태: " + em.contains(test1.getTeam()));
			System.out.println("===> clear");
			em.clear();

			Member test2 = Member.builder().id(1).build();
			Team team2 = Team.builder().id(2).build();
			test2.setTeam(team2);
			System.out.println("===> persist mem");
			em.persist(test2);
			System.out.println("fk필드 객체 persist전/persist후 동일한 transient 객체: " + (test2.getTeam() == team2));
			System.out.println("fk필드 객체는 managed 상태: " + em.contains(test2.getTeam()));
			System.out.println("===> clear");
			em.clear();

			// 2. 영속성 컨텍스트에 Team 존재
			
			Team findTeam = em.find(Team.class, 1); // 영속성 컨텍스트에 존재
			Member test3 = Member.builder().id(1).build();
			Team team3 = Team.builder().id(2).build();
			test3.setTeam(team3);
			System.out.println("===> persist mem");
			em.persist(test3);
			System.out.println("fk필드 객체 persist전/persist후 동일한 transient 객체: " + (test3.getTeam() == team3));
			System.out.println("fk필드 객체는 managed 상태: " + em.contains(test3.getTeam()));
			System.out.println("em.contains(findTeam): " + em.contains(findTeam));
			System.out.println("===> clear");
			em.clear();

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
	 * fk=notnull<br>
	 * DB에 존재하지 않는 fk에 해당하는 transient 객체 설정 후 persist<br>
	 * 1. 예외 발생 시점은 persist가 아닌 flush시점
	 */
	@Test
	void exceptionOccurFlushTimeWhenNotValidTransientFkSet1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			Member mem = Member.builder().id(1).name("mem1").build();
			// 유효하지 않은 fk
			Team noValidTeam = Team.builder().id(2).build();
			mem.setTeam(noValidTeam);
			// fk체크 결과 없음. 그러나 team필드는 flush시점전까지 유효한 값으로 변경될 수 있기 때문에 persist시 예외를 던지지않음
			// team_id=null인 insert쿼리를 생성하고 fk 2는 유효하지 않다는 것을 기억하는 상태
			System.out.println("===> persist mem");
			em.persist(mem);

			// fk 2는 유효하지 않음을 기억하기 때문에 insert쿼리를 보내지 않고 예외 발생
			// flush시점전까지 fk객체가 유효한 값으로만 재설정되면 ok
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
	 * flush호출 전 유효한 fk에 해당하는 transient 객체로 변경된 경우
	 * @see MemberPersistTest#exceptionOccurFlushTimeWhenNotValidTransientFkSet1 해당 테스트의 dirty checking 테스트1
	 */
	@Test
	void transientFkInstanceDirtyChecking1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Member mem = Member.builder().id(1).name("mem1").build();
			Team noValidTeam = Team.builder().id(2).build();
			mem.setTeam(noValidTeam);
			System.out.println("===> persist mem");
			em.persist(mem); // team_id=null인 insert쿼리 생성

			// 유효한 fk transient 객체로 재설정
			Team validTeam = Team.builder().id(1).build();
			mem.setTeam(validTeam);

			System.out.println("===> commit");
			// flush시점 dirty checking(team필드 다른 fk의 transient 객체)
			// -> fk 1 체크 쿼리(get current state) -> 유효 -> team_id=1인 update쿼리 생성
			tx.commit(); // insert,update
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/**
	 * 스냅샷 저장시 fk에 해당하는 transient 객체의 참조변경과 해당 객체의 Id필드변경까지 관리한다.
	 * @see MemberPersistTest#exceptionOccurFlushTimeWhenNotValidTransientFkSet1 해당 테스트의 dirty checking 테스트2
	 */
	@Test
	void transientFkInstanceDirtyChecking2() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			Member mem = Member.builder().id(1).name("mem1").build();
			Team noValidTeam = Team.builder().id(2).build();
			mem.setTeam(noValidTeam);
			System.out.println("===> persist mem");
			em.persist(mem);
			
			// 1. 새로운 transient 객체 재셋팅 -> 이미 체크했던 fk임
			mem.setTeam(Team.builder().id(2).build()); // flush시점 fk체크쿼리 발생x
			
			// 2. 새로운 transient 객체 재셋팅 -> 미체크했던 fk임
//			mem.setTeam(Team.builder().id(1).build()); // flush시점 fk체크쿼리 발생o
			
			// 2. 기존 transient 객체의 Id필드 변경
			mem.getTeam().setId(1); // flush 시점에 fk체크쿼리 발생O
			
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
	 * 스냅샷 비교는 한번만 진행된다!!!
	 * @see MemberPersistTest#exceptionOccurFlushTimeWhenNotValidTransientFkSet1 해당 테스트의 dirty checking 테스트3
	 */
	@Test
	void transientFkInstanceDirtyChecking3() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			Member mem = Member.builder().id(1).name("mem1").build();
			Team validTeam = Team.builder().id(1).build();
			mem.setTeam(validTeam);
			System.out.println("===> persist mem");
			em.persist(mem);
			
			mem.setTeam(Team.builder().id(3).build());
			mem.setTeam(Team.builder().id(4).build());
			
			// 마지막에 셋팅된 값이 dirty checking 비교에 사용됨
			mem.setTeam(null);
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
	 * fk가 존재하지 않아 fk엔티티를 먼저 persist해야하는 경우<br>
	 * Case: Team persist then Member persist
	 */
	@Test
	void persistTeamThenPersistMember() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			// pk2 Team를 먼저 persist하는 경우
			System.out.println("===> persist team");
			Team team = Team.builder().id(2).build();
			em.persist(team);

			Member mem = Member.builder().id(1).name("mem1").build();
			// fk필드에 managed 상태 Team을 셋팅
			mem.setTeam(team);
			System.out.println("===> persist mem");
			// fk필드에 managed 상태 Team이 셋팅된 경우 fk체크 쿼리(get current state)가 나가지 않음
			em.persist(mem);

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
	 * fk가 존재하지 않아 fk엔티티를 먼저 persist해야하는 경우<br>
	 * Case: Member persist then Team persist
	 */
	@Test
	void persistMemberThenPersistTeam() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			System.out.println("===> persist mem");
			Member mem = Member.builder().id(1).name("mem1").build();
			em.persist(mem);
			
			// pk2 Team를 나중에 persist
			System.out.println("===> persist team");
			Team team = Team.builder().id(2).build();
			em.persist(team);
			
			mem.setTeam(team);
			
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
	 * '@'ManyToOne(cascade = CascadeType.PERSIST)<br>
	 * '@'JoinColumn(name = "team_id")<br>
	 * private Team team;<br>
	 * <br>
	 * transient Team 설정후 Member persist<br>
	 * : Member persist시 Team도 persist됨<br>
	 */
	/*
	 * @ManyToOne(cascade = CascadeType.PERSIST) 설정
	 */
	@Test
	void automaticallyPersistTeamWhenPersistMember() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Team autoPersistTeam = Team.builder().id(2).name("team2").build();
			Member mem = Member.builder().id(1).name("mem1")
					// transient 객체만 동작함
					.team(autoPersistTeam).build();

			System.out.println("===> persist mem");
			em.persist(mem);

			System.out.println(em.contains(autoPersistTeam)); // true
			System.out.println(mem.getTeam() == autoPersistTeam); // true

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

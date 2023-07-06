package study.hibernate.springdatajpa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.entity.Member;
import study.hibernate.entity.Team;
import study.hibernate.springdatajpa.repository.MemberRepository;
import study.hibernate.springdatajpa.repository.TeamRepository;

/**
 * Spring Data JPA 동작 테스트
 */
//@Service
@Slf4j
public class JpaService {

	@Autowired
	private TeamRepository teamRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	

	/*
	 * @Transactional 미사용
	 */
	public void methodCallIsSingleTransaction() {
		// Team엔티티의 ID타입 =Integer(테스트 편의상 Integer설정)이며 @GeneratedValue미설정인 ID타입
		// -> save시 em.merge로 동작
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		
		// 1. 레포지토리 메서드 호출시 EntityManager프록시 객체가 내부적으로 새로운 EntityManager 생성
		// 2. EntityTransaction.begin()
		teamRepo.save(team);
		// 3. EntityTransaction.commit()
		// 4. EntityManager 해제
		
		// 1. 레포지토리 메서드 호출시 EntityManager프록시 객체가 내부적으로 새로운 EntityManager 생성
		// 2. EntityTransaction.begin()
		teamRepo.findById(1);
		// 3. EntityTransaction.commit()
		// 4. EntityManager 해제

		// findById호출시 select쿼리가 나감을 통해 새로운 영속성 컨텍스트를 사용하는 것을 검증가능
	}
	
	public void firstSuccessSecondFail() {
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		// 단일 transaction
		teamRepo.save(team);

		team.setId(null); //invalid value
		team.setName("team2");
		// 단일 transaction
		teamRepo.save(team); // 예외 발생
	}
	
	public void cantUseDirtyChecking() {
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		// 메서드 종료시 commit하고 영속성 컨텍스트(EntityManager)해제
		// -> 엔티티는 detach 상태가되므로 dirty checking 작동안함
		teamRepo.save(team);
		team.setName("dirty checking 동작안함");
	}
	
	public void saveIsSameToSaveAndFlush() {
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		log.debug("===> saveAndFlush");
		teamRepo.saveAndFlush(team);
		
		Team team2 = Team.builder()
							.id(2)
							.name("team2")
							.build();
		log.debug("===> save");
		teamRepo.save(team2);
	}
	
	public void flushDoesntDoAnything() {
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		log.debug("===> save");
		teamRepo.save(team);
		team.setName("detached 엔티티");
		
		log.debug("===> flush");
		// 프록시 객체에 의해 만들어진 새로운 EntityManager(영속성 컨텍스트)에 flush를 날리고 transaction commit하고 EntityManager를 닫는 것이라 아무것도 안일어남
		teamRepo.flush();
	}
	
	public void cantUseLazyLoading() {
		//merge 성능 이슈 테스트 감안
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		teamRepo.save(team);
		
		Member member = Member.builder()
								.id(1)
								.name("mem1")
								.team(team)
								.build();
		memberRepo.save(member);
		
		member.setId(2);
		member.setName("mem2");
		memberRepo.save(member);
		
		// Team엔티티 List<Member> <- FetchType Lazy
		Optional<Team> opt = teamRepo.findById(team.getId());
		// size()호출시 -> LazyInitializationException 발생
		log.debug("team's member count: {}" + opt.get().getMembers().size());
	}
	
	public void insertInitialData() {
		Team team1 = Team.builder()
				.id(1)
				.name("team1")
				.build();
		
		log.debug("===> save");
		teamRepo.save(team1);
		
		Member member1 = Member.builder()
								.id(1)
								.name("member1")
								.team(team1)
								.build();
		log.debug("===> save");
		memberRepo.save(member1);
		
		Member member2 = Member.builder()
								.id(2)
								.name("member2")
								.team(team1)
								.build();
		log.debug("===> save");
		memberRepo.save(member2);
	}
	
	
	/*
	 * @Transactional 사용
	 */
	@Transactional
	public void samePersistenceContextInMethod() {
		Team team = Team.builder()
							.id(1)
							.name("team1")
							.build();
		log.debug("===> save");
		teamRepo.save(team);
		
		log.debug("===> findById");
		teamRepo.findById(1); // 영속성 컨텍스트 안닫혔으므로 select 쿼리 안나감
		
		log.debug("===> commit");
	}
	
	// 지연로딩 사용가능
	@Transactional
	public void canUseLazyLoading() {
		log.debug("===> find");
		Optional<Team> opt = teamRepo.findById(1);
		if(opt.isEmpty()) {
			throw new RuntimeException("PK 1 Team Not Exist In Database");
		}
		Team findTeam = opt.get();
		log.debug("===> team.getMembers()");
		List<Member> members = findTeam.getMembers();
		log.debug("===> team.getMembers().size()");
		int count = members.size();
		log.debug("===> count: {}", count);
	}	
}

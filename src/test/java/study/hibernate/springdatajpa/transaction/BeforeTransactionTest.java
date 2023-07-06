package study.hibernate.springdatajpa.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.entity.Team;
import study.hibernate.springdatajpa.repository.TeamRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class BeforeTransactionTest {
	
	@Autowired
	private TeamRepository repo;
	
	/*
	 * ddl: create
	 */
	@BeforeTransaction
	void beforeTransaction() {
		log.debug("===> beforeTransaction");
		Team team = Team.builder().id(1).name("team1").build();
		repo.save(team);
	}

	@Test
	void test1() {
		log.debug("===> test1");
	}

	@Test
	void test2() {
		log.debug("===> test2");
	}

	@Test
	void test3() {
		log.debug("===> test3");
	}

}

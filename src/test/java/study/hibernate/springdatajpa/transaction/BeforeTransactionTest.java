package study.hibernate.springdatajpa.transaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.springdatajpa.repository.SeqEntityRepository;

/*
 * @BeforeTransaction -> @BeforeEach -> @Test -> @AfterEach -> @BeforeTransaction -> @BeforeEach -> @Test -> @AfterEach
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class BeforeTransactionTest {

	@Autowired
	private SeqEntityRepository repo;

	@BeforeEach
	void beforeEach() {
		log.debug("===> beforeEach");
	}

	@AfterEach
	void afterEach() {
		log.debug("===> afterEach");
	}

	@BeforeTransaction
	void beforeTransaction() {
		log.debug("===> beforeTransaction");
	}

	@Test
	void myTest1() {
		log.debug("===> myTest1");
	}

	@Test
	void myTest2() {
		log.debug("===> myTest2");
	}

}

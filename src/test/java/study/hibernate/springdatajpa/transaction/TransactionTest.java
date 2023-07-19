package study.hibernate.springdatajpa.transaction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/*
 * application.yml의 로깅 프로퍼티
 * -> org.springframework.orm.jpa.JpaTransactionManager 의 로그 레벨을 debug로 설정
 * -> transaction의 begin/rollback/commit 확인
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Rollback(false)
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class TransactionTest {

	@BeforeAll
	void beforeAll() {
//	static void beforeAll() {
		log.debug("=================================> @BeforeAll");
	}

	@BeforeTransaction
	void beforeTransaction() {
		log.debug("=================================> @BeforeTransaction");
	}

	@BeforeEach
	void beforeEach() {
		log.debug("=================================> @BeforeEach");
	}

	@AfterTransaction
	void afterTransaction() {
		log.debug("=================================> @AfterTransaction");
	}

	@AfterEach
	void afterEach() {
		log.debug("=================================> @AfterEach");
	}

	@AfterAll
	void AfterAll() {
//	static void AfterAll() {
		log.debug("=================================> @AfterAll");
	}

	@Test
	void test1() {
		log.debug("=================================> test1");
	}

	@Test
	void test2() {
		log.debug("=================================> test2");
	}

}

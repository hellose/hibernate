package study.hibernate.springdatajpa.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.springdatajpa.service.SequenceEntityService;

/*
 * spring.jpa.hibernate.ddl-auto: create
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class TransactionalAnnotationTest2 {

	@Autowired
	SequenceEntityService service;

	// @Transactional X -> dirty checking 동작 X
	@Test
	void test1() {
		service.saveEntity();
	}

	// @Transactional O -> dirty checking 동작 O
	@Test
	void test2() {
		service.saveEntityTrans();
	}

	// 외부 @Transaction 메서드에 의해 saveEntity()메서드 내부에서 dirty checking 동작 O
	@Test
	void test3() {
		service.transWithSaveEntity();
	}

	@Test
	void test4() {
		service.transWithSaveEntityTrans();
	}
	
	@Test
	void test5() {
		log.debug("===> test start");
		service.saveThenFind();
		log.debug("===> test end");
	}
	
	@Test
	void test6() {
		log.debug("===> test start");
		service.saveThenChange();
		log.debug("===> test end");
	}

}

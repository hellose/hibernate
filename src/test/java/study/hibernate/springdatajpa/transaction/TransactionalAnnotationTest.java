package study.hibernate.springdatajpa.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import study.hibernate.springdatajpa.service.JpaService;

/*
 * spring.jpa.hibernate.ddl-auto: create
 * 
 * @Transactional 어노테이션 유무 차이 단일 메서드 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
public class TransactionalAnnotationTest {

	@Autowired
	private JpaService service;
	
	/*
	 * 단일 일반 메서드 테스트
	 */
	
	// JpaRepository 각 메서드 호출이 단일 트랜잭션으로 처리됨
	@Test
	void test1() {
		service.methodCallIsSingleTransaction();
	}
	
	// first transaction 성공, second transaction 실패 
	@Test
	void test2() {
		service.firstSuccessSecondFail();
	}
	
	// dirty checking 사용 불가능
	@Test
	void test3() {
		service.cantUseDirtyChecking();
	}
	
	// save와 saveAndFlush는 같은 동작을함
	@Test
	void test4() {
		service.saveIsSameToSaveAndFlush();
	}
	
	// flush는 아무런 동작도 하지 않음
	@Test
	void test5() {
		service.flushDoesntDoAnything();
	}
	
	// 영속성 컨텍스트가 닫혔기 때문에 지연로딩 사용불가. 엔티티 가져올시 예외발생
	@Test
	void test6() {
		service.cantUseLazyLoading();
	}
	
	/*
	 * 단일 @Transactional 메서드 테스트
	 */
	
	// 내부에서 동일한 영속성 컨텍스트 사용
	@Test
	void test7() {
		service.samePersistenceContextInMethod();
	}
	
	/*
	 * 일반 메서드, @Transactional 메서드
	 */
	@Test
	void test8() {
		service.insertInitialData();
		service.canUseLazyLoading();
	}
}

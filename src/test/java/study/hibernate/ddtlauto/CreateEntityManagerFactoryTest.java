package study.hibernate.ddtlauto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CreateEntityManagerFactoryTest {

	private EntityManagerFactory emf;

	private static final String PERSISTENCE_UNIT_NAME = "H2";
	private static final String PROP_KEY = "hibernate.hbm2ddl.auto";

	@AfterEach
	void afterEach() {
		// EntityManagerFactory를 닫기전에 정상적으로 열려있는 상태이다.
		assertThat(emf.isOpen()).isTrue();

		// EntityManagerFactory가 정상적으로 close됨
		// create-drop 옵션 사용시 EntityManagerFactory를 정상적으로 close 해주지 않으면 종료시 프로그램 종료시 테이블
		// drop되지 않음!!!
		assertDoesNotThrow(() -> emf.close());
		emf = null;
	}

	@Test
	@DisplayName("EntityManagerFactory 인스턴스가 정상적으로 생성됨")
	void createEntityManagerOk() {
		// EntityManagerFactory가 정상적으로 생성됨
		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		});
		// EntityManagerFactory가 not null이여야 한다.
		assertThat(emf).isNotNull();
	}

	@Test
	@DisplayName("ddl.auto = create")
	void create() {
		Map<String, String> map = new HashMap<>();
		map.put(PROP_KEY, "create");

		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);
		});
		assertThat(emf).isNotNull();
	}

	@Test
	@DisplayName("ddl.auto = create-drop")
	void createDrop() {
		Map<String, String> map = new HashMap<>();
		map.put(PROP_KEY, "create-drop");

		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);
		});
		assertThat(emf).isNotNull();
	}

	@Test
	@DisplayName("ddl.auto = update")
	void update() {
		Map<String, String> map = new HashMap<>();
		map.put(PROP_KEY, "update");

		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);
		});
		assertThat(emf).isNotNull();
	}

	@Test
	@DisplayName("ddl.auto = validate")
	void validate() {
		Map<String, String> map = new HashMap<>();
		map.put(PROP_KEY, "validate");

		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);
		});
		assertThat(emf).isNotNull();
	}

	@Test
	@DisplayName("ddl.auto = none")
	void none() {
		Map<String, String> map = new HashMap<>();
		map.put(PROP_KEY, "none");

		assertDoesNotThrow(() -> {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);
		});
		assertThat(emf).isNotNull();
	}

}

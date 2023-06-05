package study.hibernate.ddtlauto;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;

public class MyHibernateClassTest {

	private EntityManagerFactory emf;

	@AfterEach
	void close() {
		assertThat(emf.isOpen()).isTrue();
		if (emf.isOpen()) {
			emf.close();
		}
		emf = null;
	}

	@DisplayName("EntityManagerFactory가 정상적으로 생성됨")
	@Test
	void persistenceDefault() {
		emf = MyHibernate.createEntityManagerFactory(null);
		assertThat(emf).isNotNull();
	}

	@DisplayName("ddl.auto = create 정상 동작")
	@Test
	void create() {
		emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
		assertThat(emf).isNotNull();
	}

	@DisplayName("ddl.auto = create-drop 정상 동작")
	@Test
	void createDrop() {
		emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE_DROP);
		assertThat(emf).isNotNull();
	}

	@DisplayName("ddl.auto = update 정상 동작")
	@Test
	void update() {
		emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
		assertThat(emf).isNotNull();
	}

	@DisplayName("ddl.auto = validate 정상 동작")
	@Test
	void validate() {
		// Entity validation 실패시 createEntityManagerFactory는 null리턴
		emf = MyHibernate.createEntityManagerFactory(DdlType.VALRIDATE);
		assertThat(emf).isNotNull();
	}

	@DisplayName("ddl.auto = none 정상 동작")
	@Test
	void none() {
		emf = MyHibernate.createEntityManagerFactory(DdlType.NONE);
		assertThat(emf).isNotNull();
	}
}

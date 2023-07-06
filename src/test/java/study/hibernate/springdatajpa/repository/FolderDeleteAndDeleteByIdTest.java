package study.hibernate.springdatajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.entity.Folder;

@SpringBootTest
@ActiveProfiles("test")
@Rollback(false)
@Transactional
@Slf4j
public class FolderDeleteAndDeleteByIdTest {

	@Autowired
	private FolderRepository repo;

	private int id;

	@BeforeTransaction
	void beforeTransaction() {
		Folder saved = repo.save(Folder.builder().name("folder1").build());
		id = saved.getId();
	}
	
	@Test
	@DisplayName("findById 조회 결과 후 값이 없으면 예외 발생")
	void deleteById() {
		repo.deleteById(100000);
	}

	@Test
	@DisplayName("인자에 transient 객체를 넘기는 경우")
	void delete1() {
		Folder tran = new Folder(id, "folderTest");
		log.debug("===> delete");
		repo.delete(tran);
	}

	@Test
	@DisplayName("인자에 managed 객체를 넘기는 경우")
	void delete2() {
		Folder tran = new Folder(1);
		log.debug("===> delete");
		repo.delete(tran);
	}
}
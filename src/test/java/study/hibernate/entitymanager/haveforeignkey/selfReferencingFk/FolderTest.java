package study.hibernate.entitymanager.haveforeignkey.selfReferencingFk;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.Folder;

/**
 * Member엔티티와 Team엔티티의 경우 Member persist시 fk Team엔티티가 transient상태였다면 fk 체크쿼리가 나갔지만
 * Folder엔티티의 경우 Folder persist시 fk Folder엔티티가 같은 엔티티라 fk 체크쿼리가 안나가는 것으로 추정
 */
@Slf4j
public class FolderTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;
	
	private int rootFolderId;

	@BeforeEach
	void beforeEach() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Folder rootFolder = Folder.builder().name("최상위").build();
			em.persist(rootFolder);
			rootFolderId = rootFolder.getId();
			
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
			tx = null;
			em = null;
			emf = null;
			log.debug("===> beforeEach");
		}
	}

	@AfterEach
	void afterEach() {
		tx = null;
		em = null;
		emf = null;
	}

	/*
	 * self-referencing fk인 경우 persist호출시 fk 체크 쿼리 안나감
	 */
	@Test
	void test1() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Folder lvlOneFolder1 = Folder.builder().name("레벨1-1").build();
			
			//transient 엔티티 설정
			lvlOneFolder1.setParentFolder(Folder.builder()
												.id(rootFolderId)
												.build());

			//fk 체크 쿼리 나가지않음!!!
			em.persist(lvlOneFolder1);

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/*
	 * persist후 fk의 transient객체는 그대로 transient상태를 유지
	 */
	@Test
	void test2() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Folder lvlOneFolder1 = Folder.builder().name("레벨1-1").build();
			
			//transient 엔티티 설정
			lvlOneFolder1.setParentFolder(Folder.builder()
												.id(rootFolderId)
												.name("fk를 설정하기 위한 transient객체")
												.build());

			em.persist(lvlOneFolder1);
			em.flush();
			
			//fk 객체  transient 상태 유지
			log.debug("===> {}", em.contains(lvlOneFolder1.getParentFolder()));
			log.debug("===> {}", lvlOneFolder1.getParentFolder().getName());
			
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/*
	 * find시 부모 Folder 즉시로딩,지연로딩 테스트
	 */
	@Test
	void test3() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			//level 1 folder - persist
			Folder lvlOneFolder1 = Folder.builder().name("레벨1-1").build();
			lvlOneFolder1.setParentFolder(Folder.builder()
												.id(rootFolderId)
												.build());
			em.persist(lvlOneFolder1);
			
			//level 2 folder - persist
			Folder lvlTwoFolder1 = Folder.builder().name("레벨2-1").build();
			lvlTwoFolder1.setParentFolder(Folder.builder()
												.id(lvlOneFolder1.getId())
												.build());
			em.persist(lvlTwoFolder1);
			em.flush();
			em.clear();
			
			log.debug("=========================================================> find");
			em.find(Folder.class, lvlTwoFolder1.getId());
			
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/*
	 * transient엔티티 fk를 managed엔티티로 변경 테스트
	 */
	@Test
	void test4() {
		try {
			emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Folder transientRoot = Folder.builder()
											.id(rootFolderId)
											.build();
			
			Folder lvlOneFolder1 = Folder.builder().name("레벨1-1").build();
			lvlOneFolder1.setParentFolder(transientRoot);
			em.persist(lvlOneFolder1);
			
			em.flush();
			
			em.refresh(lvlOneFolder1);
			log.debug("===> fk transient객체 유지: {}", lvlOneFolder1.getParentFolder() == transientRoot);
			log.debug("===> fk 객체 타입: {}", lvlOneFolder1.getParentFolder().getClass().getSimpleName());
			
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

package study.hibernate.orderby;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.extern.slf4j.Slf4j;
import study.hibernate.ddlauto.MyHibernate;
import study.hibernate.ddlauto.MyHibernate.DdlType;
import study.hibernate.entity.orderby.ChildTable;
import study.hibernate.entity.orderby.ChildTable.ChildTableBuilder;
import study.hibernate.entity.orderby.ParentTable;

@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class OrderByAnnotationTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	private List<Integer> idList = new ArrayList<>();

	int generateRandomNumber() {
		return (int) (Math.random() * 1000 + 1);
	}

	@BeforeEach
	void beforeEach() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.CREATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				ParentTable p1 = new ParentTable();
				em.persist(p1);
				idList.add(p1.getId());

				ParentTable p2 = new ParentTable();
				em.persist(p2);
				idList.add(p2.getId());

				ChildTableBuilder builder = ChildTable.builder();

				for (int i = 0; i < 10; i++) {
					// fk is p1
					em.persist(builder.orderTestOne(generateRandomNumber())
							.orderTestTwo(generateRandomNumber())
							.parent(p1)
							.build());
					// fk is p2
					em.persist(builder.orderTestOne(generateRandomNumber())
							.orderTestTwo(generateRandomNumber())
							.parent(p2)
							.build());
				}

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
				tx = null;
				em = null;
				emf = null;
			}
		});
	}

	@AfterEach
	void afterEach() {
		tx = null;
		em = null;
		emf = null;
	}

	@Test
	void oneToManyListOrderByTest() {
		assertDoesNotThrow(() -> {
			try {
				emf = MyHibernate.createEntityManagerFactory(DdlType.UPDATE);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();

				for (Integer id : idList) {
					ParentTable p = em.find(ParentTable.class, id);
					log.debug("자식 조회");
					for (ChildTable c : p.getChildren()) {
						log.debug("parent id: {}, id: {}, first: {}, second: {}", 
								p.getId(), c.getId(), c.getOrderTestOne(), c.getOrderTestTwo());
					}
				}

				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				throw new RuntimeException(e);
			} finally {
				em.close();
				emf.close();
			}
		});
	}
}
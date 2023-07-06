package study.hibernate.springdatajpa.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import study.hibernate.entity.SeqEntity;
import study.hibernate.springdatajpa.repository.SeqEntityRepository;

//@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceEntityService {

	private final SeqEntityRepository repo;

	public SeqEntity saveEntity() {
		SeqEntity entity = new SeqEntity();
		repo.save(entity);
		log.debug("===> after save");
		entity.setName("변경1");
		return entity;
	}

	@Transactional
	public SeqEntity saveEntityTrans() {
		SeqEntity entity = new SeqEntity();
		repo.save(entity);
		entity.setName("변경1");
		return entity;
	}

	@Transactional
	public void transWithSaveEntity() {
		saveEntity();
	}

	@Transactional
	public void transWithSaveEntityTrans() {
		saveEntityTrans();
	}

	@Transactional
	public void saveThenFind() {
		log.debug("===> service start");
		SeqEntity saved = saveEntity();
		log.debug("===> middle");
		findEntity(saved.getId());
		log.debug("===> service end");
	}
	
	@Transactional
	public void saveThenChange() {
		log.debug("===> service start");
		SeqEntity saved = saveEntity();
		log.debug("===> middle");
		saved.setName("변경2");
		log.debug("===> service end");
	}

	public void findEntity(Integer id) {
		repo.findById(id);
		log.debug("===> after findById");
	}

	@Transactional
	public List<SeqEntity> findAllSequenceEntity() {
		return repo.findAll();
	}

}

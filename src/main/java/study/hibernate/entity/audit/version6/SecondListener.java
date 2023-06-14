package study.hibernate.entity.audit.version6;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecondListener {

	@PrePersist
	public void prePersist(Object obj) {
		log.debug("===> SecondListener.prePersist()");
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		log.debug("===> SecondListener.preUpdate()");
	}
}

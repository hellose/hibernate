package study.hibernate.entity.audit.version6;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirstListener {

	@PrePersist
	public void prePersist(AuditVersion6 entity) {
		log.debug("===> FirstListener.prePersist()");
	}

	@PreUpdate
	public void preUpdate(AuditVersion6 entity) {
		log.debug("===> FirstListener.preUpdate()");
	}
}

package study.hibernate.entity.audit.version4and5;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateAuditableListener {

	@PrePersist
	public void prePersist(Object obj) {
		log.debug("===> DateAuditableInterfaceListener.prePersist()");
		DateAuditable dateAuditable = (DateAuditable) obj;
		dateAuditable.setCreatedTime();
		dateAuditable.setUpdatedTime();
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		log.debug("===> DateAuditableInterfaceListener.preUpdate()");
		DateAuditable dateAuditable = (DateAuditable) obj;
		dateAuditable.setUpdatedTime();
	}
}

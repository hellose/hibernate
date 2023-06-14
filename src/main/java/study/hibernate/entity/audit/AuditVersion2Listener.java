package study.hibernate.entity.audit;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AuditVersion2Listener {

	@PrePersist
	public void prePersist(Object obj) {
		AuditVersion2 en = (AuditVersion2) obj;

		LocalDateTime now = LocalDateTime.now();
		en.setCreatedTime(now);
		en.setUpdatedTime(now);
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		AuditVersion2 en = (AuditVersion2) obj;

		LocalDateTime now = LocalDateTime.now();
		en.setUpdatedTime(now);
	}
}

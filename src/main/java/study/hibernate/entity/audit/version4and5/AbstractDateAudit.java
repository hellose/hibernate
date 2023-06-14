package study.hibernate.entity.audit.version4and5;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(DateAuditableListener.class)
public abstract class AbstractDateAudit {

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdTime;

	@Column(nullable = false)
	private LocalDateTime updatedTime;

}

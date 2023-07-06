package study.hibernate.entity.audit.version4and5;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/*
 * Custom Entity Listener 사용
 */
//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(DateAuditableListener.class)
@Slf4j
public class AuditVersion4 implements DateAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdTime;

	@Column(nullable = false)
	private LocalDateTime updatedTime;

	@Override
	public void setCreatedTime() {
		log.debug("===> AuditVersion4.setCreatedTime()");
		LocalDateTime now = LocalDateTime.now();
		this.createdTime = now;
		this.updatedTime = now;
	}

	@Override
	public void setUpdatedTime() {
		log.debug("===> AuditVersion4.setUpdatedTime()");
		LocalDateTime now = LocalDateTime.now();
		this.updatedTime = now;
	}

}

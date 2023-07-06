package study.hibernate.entity.audit.version4and5;

import java.time.LocalDateTime;

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
 * 추상 클래스 및 @MappedSuperclass로 변경
 */
//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
//@ExcludeSuperclassListeners -> AbstractDateAudit에 등록된 @EntityListeners가 동작하지 않음
public class AuditVersion5 extends AbstractDateAudit implements DateAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@Override
	public void setCreatedTime() {
		log.debug("===> AuditVersion5.setCreatedTime()");
		LocalDateTime now = LocalDateTime.now();
		super.setCreatedTime(now);
		super.setUpdatedTime(now);
	}

	@Override
	public void setUpdatedTime() {
		log.debug("===> AuditVersion5.setUpdatedTime()");
		LocalDateTime now = LocalDateTime.now();
		super.setUpdatedTime(now);
	}

}

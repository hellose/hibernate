package study.hibernate.entity.audit.version6;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/*
 * Multiple Entity Listener 테스트
 * 호출 순서: FirstListener -> SecondListener -> AuditVersion6
 */
//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@EntityListeners({ FirstListener.class, SecondListener.class })
public class AuditVersion6 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@PrePersist
	public void prePersist() {
		log.debug("===> AuditVersion6.prePersist()");
	}

	@PreUpdate
	public void preUpdate() {
		log.debug("===> AuditVersion6.preUpdate()");
	}
}

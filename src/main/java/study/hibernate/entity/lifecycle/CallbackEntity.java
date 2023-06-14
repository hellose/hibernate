package study.hibernate.entity.lifecycle;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CallbackEntity {
	@Id
	private Integer id;

	private String col;

	// 1.persist 2.merge시 DB에 존재하지 않는 경우
	// -> transient상태 엔티티가 managed상태로 변경되기 직전 호출
	@PrePersist
	public void prePersist() {
		log.debug("===> @PrePersist");
		// transient 객체의 필드를 변경 후 persist가 호출되는 것과 동일 -> insert쿼리만 생성됨
//		this.col = "prePersist에서 변경";
	}

	// insert 쿼리 발생 후 호출
	@PostPersist
	public void postPersist() {
		log.debug("===> @PostPersist");
	}

	// update 쿼리 발생 전 호출
	@PreUpdate
	public void preUpdate() {
		log.debug("===> @PreUpdate");
		// 필드 변경시 변경된 필드 값으로 스냅샷으로 기록됨
//		this.col = "preUpdate에서 필드 값 변경";
	}

	// update 쿼리 발생 후 호출
	@PostUpdate
	public void postUpdate() {
		log.debug("===> @PostUpdate");
	}
	
	// DB에서 조회하여 영속성 컨텍스트에 로드되는 경우 호출
	// persist를 통해 영속성 컨텍스트에 로드되는 경우는 호출되지 않음
	@PostLoad
	public void postLoad() {
		log.debug("===> @PostLoad");
	}

	// remove
	@PreRemove
	public void preRemove() {
		log.debug("===> @PreRemove");
	}

	// delete 쿼리 발생 후 호출
	@PostRemove
	public void postRemove() {
		log.debug("===> @PostRemove");
	}

}

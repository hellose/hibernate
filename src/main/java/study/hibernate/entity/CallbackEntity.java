package study.hibernate.entity;

import javax.annotation.PreDestroy;
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

	// persist 동작 수행 전 호출
	@PrePersist
	public void prePersist() {
		log.debug("===> @PrePersist");

		// transient 상태 객체의 필드를 변경 후 persist가 호출되는 것과 동일 -> insert쿼리만 생성됨
//		this.col = "prePersist에서 변경";
	}

	// insert 쿼리 flush 후 호출
	@PostPersist
	public void postPersist() {
		log.debug("===> @PostPersist");
		
//		this.col = "postPersist에서 변경";
	}

	// update 쿼리 flush 전 호출
	@PreUpdate
	public void preUpdate() {
		log.debug("===> @PreUpdate");
		this.col = "preUpdate에서 필드 값 변경";
	}

	// update 쿼리 flush 후 호출
	@PostUpdate
	public void postUpdate() {
		log.debug("===> @PostUpdate");
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("===> @PreDestroy");
	}

	@PostLoad
	public void postLoad() {
		log.debug("===> @PostLoad");
	}

	@PreRemove
	public void preRemove() {
		log.debug("===> @PreRemove");
	}

	@PostRemove
	public void postRemove() {
		log.debug("===> @PostRemove");
	}

}

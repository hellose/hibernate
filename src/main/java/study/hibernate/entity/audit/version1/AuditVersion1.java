package study.hibernate.entity.audit.version1;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/*
 * @PrePersist, @PostUpdate를 통해 직접 구현
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditVersion1 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdTime;

	@Column(nullable = false)
	private LocalDateTime updatedTime;

	@PrePersist
	public void prePersist() {
		this.createdTime = LocalDateTime.now();
		this.updatedTime = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedTime = LocalDateTime.now();
	}
}

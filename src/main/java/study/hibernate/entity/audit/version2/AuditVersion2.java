package study.hibernate.entity.audit.version2;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Hibernate에서 제공하는 @CreationTimestamp, @UpdateTimestamp 사용
 */
//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditVersion2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdTime;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedTime;
}

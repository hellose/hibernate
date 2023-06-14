package study.hibernate.entity.audit.version3;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * @CreatedDate, @LastModifiedDate 어노테이션이 붙은 필드는 Spring Data JPA에서 제공하는 AuditingEntityListener를 통해 auditing가능
 * AuditingEntityListener가 동작하기 위해서는 Spring Data JPA 엔티티 Auditing기능 활성화에 해당하는 @EnableJpaAuditing 활성화 필요
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditVersion3 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String col;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdTime;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedTime;

}

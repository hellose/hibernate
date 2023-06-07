package study.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * NotNull VS Column(nullable=false)
 */
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationEntity {

	@Id
	private Integer id;

	@NotNull
	private String notNullCol;

	@Column(nullable = false)
	private String nullableFalseCol;
}

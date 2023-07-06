package study.hibernate.entity.column;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
//@DynamicInsert
public class ColumnInsertUpdatableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String insertable;

	// insertable = false -> insert문에 미포함
	// updatable = false -> update문에 미포함

	@Column(insertable = false)
	private String notInsertable;

	private String updatable;

	@Column(updatable = false)
	private String notUpdatable;

	// @ColumnDefault + @Column(insertable, updatable) 테스트
	@Column(insertable = false)
	@ColumnDefault("'default'")
	private String notInsertableDefault;

	@Column(updatable = false)
	@ColumnDefault("'default'")
	private String notUpdatableDefault;

}

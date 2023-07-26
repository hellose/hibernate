package study.hibernate.entity.orderby;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "childTableSeqGenerator", allocationSize = 1, initialValue = 1, sequenceName = "child_table_seq")
public class ChildTable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "childTableSeqGenerator")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private ParentTable parent;

	// order by 테스트
	private Integer orderTestOne;
	private Integer orderTestTwo;
}
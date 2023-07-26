package study.hibernate.entity.orderby;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@SequenceGenerator(name = "parentTableSeqGenerator", allocationSize = 1, initialValue = 1, sequenceName = "parent_table_seq")
public class ParentTable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parentTableSeqGenerator")
	private Integer id;

	@OneToMany(mappedBy = "parent")
	@OrderBy("orderTestOne asc, orderTestTwo asc")
	private List<ChildTable> children;

}

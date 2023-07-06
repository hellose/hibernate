package study.hibernate.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@SequenceGenerator(name = "seq_entity_sequence_generator", allocationSize = 1, initialValue = 1, sequenceName = "seq_entity_sequence")
public class SeqEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_entity_sequence_generator")
	private Integer id;
	
	private String name;
}

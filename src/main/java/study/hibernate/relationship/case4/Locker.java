package study.hibernate.relationship.case4;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Locker {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
}

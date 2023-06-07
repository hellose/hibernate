package study.hibernate.entitymanager.haveforeignkey;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "members" })
@Entity
public class Team {
	@Id
	private Integer id;

	@Column(nullable = true)
	private String name;

	@OneToMany(mappedBy = "team")
	private List<Member> members;
}
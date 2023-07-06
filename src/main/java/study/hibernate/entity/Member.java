package study.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
@ToString(exclude = { "team" })
//@Entity
public class Member {

	@Id
	private Integer id;

	@Column(nullable = true)
	private String name;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

}

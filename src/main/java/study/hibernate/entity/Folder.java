package study.hibernate.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 자기 테이블의 pk를 참조하는 fk가 자기 테이블에 있는 경우
 */
//@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "FolderGenerator", sequenceName = "folder_seq")
public class Folder {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FolderGenerator")
	private Integer id;

	@Column(nullable = true)
	private String name;

	/*
	 * 즉시로딩인 경우 현재 Folder로딩시 모든 상위 Folder들이 로딩됨
	 * 지연로딩인 경우 현재 Folder로딩 후 부모 로딩시 한 레벨 위 부모 Folder 딱 한개만 로딩됨
	 */
//	@ManyToOne
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private Folder parentFolder;

	@OneToMany(mappedBy = "parentFolder")
	private List<Folder> children;

	public Folder(Integer id) {
		this.id = id;
	}

	public Folder(String name) {
		this.name = name;
	}

	public Folder(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
}

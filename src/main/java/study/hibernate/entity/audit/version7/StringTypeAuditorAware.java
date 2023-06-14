package study.hibernate.entity.audit.version7;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class StringTypeAuditorAware implements AuditorAware<String> {

	private String userName = "사용자";
	private int number = 1;

	@Override
	public Optional<String> getCurrentAuditor() {
		String result = userName + number;
		number++;
		return Optional.of(result);
	}

}

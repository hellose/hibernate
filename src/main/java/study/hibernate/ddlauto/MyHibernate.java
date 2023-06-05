package study.hibernate.ddlauto;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.RequiredArgsConstructor;

public class MyHibernate {

	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "H2";

	@RequiredArgsConstructor
	public static enum DdlType {
		CREATE("create"), 
		CREATE_DROP("create-drop"), 
		UPDATE("update"), 
		VALRIDATE("validate"),
		NONE("none");
		private final String propertyValue;
		private static final String propertyName = "hibernate.hbm2ddl.auto";
	}

	public static EntityManagerFactory createEntityManagerFactory(DdlType ddlType) {
		return createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME, ddlType);
	}

	private static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, DdlType ddlType) {
		if (ddlType == null) {
			return Persistence.createEntityManagerFactory(persistenceUnitName);
		}

		Map<String, String> map = new HashMap<>();
		map.put(DdlType.propertyName, ddlType.propertyValue);
		
		return Persistence.createEntityManagerFactory(persistenceUnitName, map);
	}
	
}

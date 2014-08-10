package org.jboss.tools.hibernate.spi;

import java.util.List;

public interface ICriteria {

	ICriteria createCriteria(String associationPath, String alias);
	void setMaxResults(int intValue);
	List<Object> list();

}

package org.jboss.tools.hibernate.spi;

import java.util.List;

import org.hibernate.type.Type;

public interface IQuery {

	List<Object> list();
	void setMaxResults(int intValue);
	void setParameter(int pos, Object value, Type type);
	void setParameterList(String name, List<Object> list, Type type);
	void setParameter(String name, Object value, Type type);
	String[] getReturnAliases();
	Type[] getReturnTypes();

}

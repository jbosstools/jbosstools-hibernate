package org.jboss.tools.hibernate.runtime.spi;

import java.util.List;

public interface IQuery {

	List<Object> list();
	void setMaxResults(int intValue);
	void setParameter(int pos, Object value, IType type);
	void setParameterList(String name, List<Object> list, IType type);
	void setParameter(String name, Object value, IType type);
	String[] getReturnAliases();
	IType[] getReturnTypes();

}

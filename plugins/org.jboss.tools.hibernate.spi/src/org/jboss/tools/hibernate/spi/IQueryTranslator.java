package org.jboss.tools.hibernate.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface IQueryTranslator {

	boolean isManipulationStatement();
	Set<Serializable> getQuerySpaces();
	IType[] getReturnTypes();
	List<String> collectSqlStrings();

}

package org.jboss.tools.hibernate.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.hibernate.type.Type;

public interface IQueryTranslator {

	boolean isManipulationStatement();
	Set<Serializable> getQuerySpaces();
	Type[] getReturnTypes();
	List<String> collectSqlStrings();

}

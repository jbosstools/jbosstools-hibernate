package org.hibernate.eclipse.console.model;

public interface ITableFilter {

	void setExclude(Boolean exclude);

	void setMatchCatalog(String catalog);

	void setMatchSchema(String schema);

	void setMatchName(String name);

	Boolean getExclude();

	String getMatchCatalog();

	String getMatchSchema();

	String getMatchName();

}

package org.jboss.tools.hibernate.search.runtime.spi;

public interface IQueryParser {
	ILuceneQuery parse(String request);
}

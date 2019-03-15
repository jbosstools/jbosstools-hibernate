package org.jboss.tools.hibernate.search.runtime.spi;

import java.util.Set;

public interface ISearchFactory {
	Set<Class<?>> getIndexedTypes();
	IIndexReader getIndexReader(Class<?>... entities);
}

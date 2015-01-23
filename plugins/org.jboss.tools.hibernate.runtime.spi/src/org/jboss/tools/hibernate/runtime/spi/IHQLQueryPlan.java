package org.jboss.tools.hibernate.runtime.spi;


public interface IHQLQueryPlan {

	IQueryTranslator[] getTranslators();

}

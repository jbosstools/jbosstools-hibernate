package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;

public abstract class AbstractNamingStrategyFacade 
extends AbstractFacade 
implements INamingStrategy {

	public AbstractNamingStrategyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String collectionTableName(
			String ownerEntityName, 
			String name,
			String targetEntityName, 
			String name2, 
			String propName) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"collectionTableName", 
				new Class[] { 
					String.class, 
					String.class, 
					String.class, 
					String.class, 
					String.class },
				new Object[] { 
					ownerEntityName, 
					name, 
					targetEntityName, 
					name2, 
					propName });
	}

	@Override
	public String columnName(String name) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"columnName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}

	@Override
	public String propertyToColumnName(String name) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"propertyToColumnName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}

}

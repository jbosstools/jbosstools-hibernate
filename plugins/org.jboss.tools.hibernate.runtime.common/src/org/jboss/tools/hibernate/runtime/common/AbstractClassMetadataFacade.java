package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractClassMetadataFacade 
extends AbstractFacade 
implements IClassMetadata {

	protected IType[] propertyTypes = null;
	protected IType identifierType = null;

	public AbstractClassMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getEntityName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getIdentifierPropertyName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getIdentifierPropertyName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String[] getPropertyNames() {
		return (String[])Util.invokeMethod(
				getTarget(), 
				"getPropertyNames", 
				new Class[] {}, 
				new Object[] {});
	}

	protected void initializePropertyTypes() {
		Object[] originTypes = (Object[])Util.invokeMethod(
				getTarget(), 
				"getPropertyTypes", 
				new Class[] {}, 
				new Object[] {});
		ArrayList<IType> propertyTypes = new ArrayList<IType>(originTypes.length);
		for (Object type : originTypes) {
			propertyTypes.add(getFacadeFactory().createType(type));
		}
		this.propertyTypes = propertyTypes.toArray(new IType[originTypes.length]);
	}

}

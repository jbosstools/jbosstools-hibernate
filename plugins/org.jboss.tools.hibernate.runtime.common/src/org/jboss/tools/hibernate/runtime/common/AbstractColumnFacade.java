package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractColumnFacade 
extends AbstractFacade 
implements IColumn {

	public AbstractColumnFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public Integer getSqlTypeCode() {
		return (Integer)Util.invokeMethod(
				getTarget(), 
				"getSqlTypeCode", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getSqlType() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getSqlType", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getLength() {
		return (Integer)Util.invokeMethod(
				getTarget(), 
				"getLength", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getDefaultLength() {
		return (Integer)Util.getFieldValue(getColumnClass(), "DEFAULT_LENGTH", null);
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
	}

}

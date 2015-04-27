package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractColumnFacade 
extends AbstractFacade 
implements IColumn {

	private IValue value = null;

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
		return (Integer)Util.getFieldValue(
				getColumnClass(), 
				"DEFAULT_LENGTH", 
				null);
	}
	
	@Override
	public int getPrecision() {
		return (Integer)Util.invokeMethod(
				getTarget(), 
				"getPrecision", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getDefaultPrecision() {
		return (Integer)Util.getFieldValue(
				getColumnClass(), 
				"DEFAULT_PRECISION", 
				null);
	}

	@Override
	public int getScale() {
		return (Integer)Util.invokeMethod(
				getTarget(), 
				"getScale", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public int getDefaultScale() {
		return (Integer)Util.getFieldValue(
				getColumnClass(), 
				"DEFAULT_SCALE", 
				null);
	}

	@Override
	public boolean isNullable() {
		return (Boolean)Util.invokeMethod(
				getTarget(), 
				"isNullable", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IValue getValue() {
		Object targetValue = getTargetValue();
		if (targetValue != null && value == null) {
			value = getFacadeFactory().createValue(targetValue);
		}
		return value;
	}

	@Override
	public boolean isUnique() {
		return (Boolean)Util.invokeMethod(
				getTarget(), 
				"isUnique", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getSqlType(IDialect dialect, IMapping mapping) {
		Object dialectTarget = Util.invokeMethod(
				dialect, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Object mappingTarget = Util.invokeMethod(
				mapping, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		return (String)Util.invokeMethod(
				getTarget(), 
				"getSqlType", 
				new Class[] { getDialectClass(),  getMappingClass() }, 
				new Object[] { dialectTarget, mappingTarget });
	}

	@Override
	public void setSqlType(String sqlType) {
		Util.invokeMethod(
				getTarget(), 
				"setSqlType", 
				new Class[] { String.class }, 
				new Object[] { sqlType });
	}

	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getDialectClass() {
		return Util.getClass(getDialectClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getMappingClass() {
		return Util.getClass(getMappingClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
	}
	
	protected String getDialectClassName() {
		return "org.hibernate.dialect.Dialect";
	}
	
	protected String getMappingClassName() {
		return "org.hibernate.engine.spi.Mapping";
	}
	
	private Object getTargetValue() {
		return Util.invokeMethod(
				getTarget(), 
				"getValue", 
				new Class[] {}, 
				new Object[] {});
	}

}

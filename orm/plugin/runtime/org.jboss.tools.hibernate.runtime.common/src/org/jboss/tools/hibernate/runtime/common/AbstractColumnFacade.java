package org.jboss.tools.hibernate.runtime.common;

import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
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
	public long getLength() {
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

	public String getSqlType(IConfiguration configuration) {
		String result = null;
		String dialectKey = (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DIALECT", 
				null);		
		String dialectName = configuration.getProperty(dialectKey);
		if (dialectName != null) {
			Object dialectTarget = Util.invokeMethod(
					getDialectFactoryClass(), 
					"buildDialect", 
					new Class[] { Properties.class },	 
					new Object[] { configuration.getProperties() });
			if (dialectTarget != null) {
				Object configurationTarget = Util.invokeMethod(
						configuration, 
						"getTarget", 
						new Class[] {}, 
						new Object[] {});
				Object mappingTarget = Util.invokeMethod(
						configurationTarget, 
						"buildMapping", 
						new Class[] {}, 
						new Object[] {});
				result = (String)Util.invokeMethod(
						getTarget(), 
						"getSqlType", 
						new Class[] { getDialectClass(),  getMappingClass() }, 
						new Object[] { dialectTarget, mappingTarget });
			}
		}
		return result;
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
	
	protected Class<?> getDialectFactoryClass() {
		return Util.getClass(getDialectFactoryClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getEnvironmentClass() {
		return Util.getClass(getEnvironmentClassName(), getFacadeFactoryClassLoader());
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
	
	protected String getDialectFactoryClassName() {
		return "org.hibernate.dialect.resolver.DialectFactory";
	}
	
	protected String getEnvironmentClassName() {
		return "org.hibernate.cfg.Environment";
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

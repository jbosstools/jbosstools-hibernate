package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class ColumnFacadeImpl extends AbstractColumnFacade {
	
	private IValue value = null;
	
	public ColumnFacadeImpl(
			IFacadeFactory facadeFactory, 
			Column column) {
		super(facadeFactory, column);
	}	

	public Column getTarget() {
		return (Column)super.getTarget();
	}

	@Override
	public boolean isNullable() {
		return getTarget().isNullable();
	}

	@Override
	public IValue getValue() {
		if (getTarget().getValue() != null && value == null) {
			value = getFacadeFactory().createValue(getTarget().getValue());
		}
		return value;
	}

	@Override
	public boolean isUnique() {
		return getTarget().isUnique();
	}

	@Override
	public String getSqlType(IDialect dialect, IMapping mapping) {
		assert dialect instanceof IFacade;
		assert mapping instanceof IFacade;
		return getTarget().getSqlType(
				(Dialect)((IFacade)dialect).getTarget(), 
				(Mapping)((IFacade)mapping).getTarget());
	}

	@Override
	public void setSqlType(String sqlType) {
		getTarget().setSqlType(sqlType);
	}

}

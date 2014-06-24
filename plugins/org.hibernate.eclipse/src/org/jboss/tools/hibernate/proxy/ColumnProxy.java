package org.jboss.tools.hibernate.proxy;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IDialect;
import org.jboss.tools.hibernate.spi.IMapping;
import org.jboss.tools.hibernate.spi.IValue;

public class ColumnProxy implements IColumn {
	
	private Column target = null;
	private IValue value = null;

	public ColumnProxy(Object column) {
		target = (Column)column;
	}

	public Column getTarget() {
		return target;
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@Override
	public Integer getSqlTypeCode() {
		return target.getSqlTypeCode();
	}

	@Override
	public String getSqlType() {
		return target.getSqlType();
	}

	@Override
	public int getLength() {
		return target.getLength();
	}

	@Override
	public int getDefaultLength() {
		return Column.DEFAULT_LENGTH;
	}

	@Override
	public int getPrecision() {
		return target.getPrecision();
	}

	@Override
	public int getDefaultPrecision() {
		return Column.DEFAULT_PRECISION;
	}

	@Override
	public int getScale() {
		return target.getScale();
	}

	@Override
	public int getDefaultScale() {
		return Column.DEFAULT_SCALE;
	}

	@Override
	public boolean isNullable() {
		return target.isNullable();
	}

	@Override
	public IValue getValue() {
		if (target.getValue() != null && value == null) {
			value = new ValueProxy(target.getValue());
		}
		return value;
	}

	@Override
	public boolean isUnique() {
		return target.isUnique();
	}

	@Override
	public String getSqlType(IDialect dialect, IMapping mapping) {
		assert dialect instanceof DialectProxy;
		assert mapping instanceof MappingProxy;
		return target.getSqlType(
				((DialectProxy)dialect).getTarget(), 
				((MappingProxy)mapping).getTarget());
	}

	@Override
	public void setSqlType(String sqlType) {
		target.setSqlType(sqlType);
	}

}

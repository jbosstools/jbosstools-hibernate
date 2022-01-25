package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import java.util.Date;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JdbcDateJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.DateJdbcType;

public class DateType extends AbstractSingleColumnStandardBasicType<Date> {

	public static final DateType INSTANCE = new DateType();

	public DateType() {
		super(DateJdbcType.INSTANCE, JdbcDateJavaTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "date";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), java.sql.Date.class.getName() };
	}

}

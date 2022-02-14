package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.JdbcTimestampJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;

public class TimestampType extends AbstractSingleColumnStandardBasicType<Date> {

	public static final TimestampType INSTANCE = new TimestampType();

	public TimestampType() {
		super(TimestampJdbcType.INSTANCE, JdbcTimestampJavaType.INSTANCE);
	}

	protected TimestampType(JdbcType jdbcType, JavaType<Date> javaTypeDescriptor) {
		super(jdbcType, javaTypeDescriptor);
	}

	@Override
	public String getName() {
		return "timestamp";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Timestamp.class.getName(), Date.class.getName() };
	}

}

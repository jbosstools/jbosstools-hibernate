package org.jboss.tools.hibernate.runtime.v_6_2.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.DoubleJavaType;
import org.hibernate.type.descriptor.jdbc.DoubleJdbcType;

public class DoubleType extends AbstractSingleColumnStandardBasicType<Double> {
	public static final DoubleType INSTANCE = new DoubleType();

	public DoubleType() {
		super(DoubleJdbcType.INSTANCE, DoubleJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "double";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), double.class.getName(), Double.class.getName() };
	}
}

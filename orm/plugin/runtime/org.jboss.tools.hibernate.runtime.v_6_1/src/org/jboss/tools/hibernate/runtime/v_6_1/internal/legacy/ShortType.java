package org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ShortJavaType;
import org.hibernate.type.descriptor.jdbc.SmallIntJdbcType;

public class ShortType extends AbstractSingleColumnStandardBasicType<Short> {

	public static final ShortType INSTANCE = new ShortType();

	private static final Short ZERO = (short) 0;

	public ShortType() {
		super(SmallIntJdbcType.INSTANCE, ShortJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "short";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), short.class.getName(), Short.class.getName() };
	}

}

package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ByteJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

public class ByteType extends AbstractSingleColumnStandardBasicType<Byte> {

	public static final ByteType INSTANCE = new ByteType();

	public ByteType() {
		super(TinyIntJdbcType.INSTANCE, ByteJavaTypeDescriptor.INSTANCE);
	}

	@Override
	public String getName() {
		return "byte";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), byte.class.getName(), Byte.class.getName() };
	}
}

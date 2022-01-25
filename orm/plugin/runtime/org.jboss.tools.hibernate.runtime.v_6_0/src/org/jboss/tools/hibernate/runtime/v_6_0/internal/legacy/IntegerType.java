package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.IntegerJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

public class IntegerType extends AbstractSingleColumnStandardBasicType<Integer> {

	public static final IntegerType INSTANCE = new IntegerType();

	public IntegerType() {
		super( IntegerJdbcType.INSTANCE, IntegerJavaTypeDescriptor.INSTANCE );
	}

	@Override
	public String getName() {
		return "integer";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] {getName(), int.class.getName(), Integer.class.getName()};
	}

}

package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import java.math.BigInteger;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.BigIntegerJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;

public class BigIntegerType extends AbstractSingleColumnStandardBasicType<BigInteger> {

	public static final BigIntegerType INSTANCE = new BigIntegerType();

	public BigIntegerType() {
		super(NumericJdbcType.INSTANCE, BigIntegerJavaTypeDescriptor.INSTANCE);
	}

	@Override
	public String getName() {
		return "big_integer";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}

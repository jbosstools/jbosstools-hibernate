package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.StringJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class StringType extends AbstractSingleColumnStandardBasicType<String> implements AdjustableBasicType<String> {

	public static final StringType INSTANCE = new StringType();

	public StringType() {
		super(VarcharJdbcType.INSTANCE, StringJavaTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "string";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}

package org.jboss.tools.hibernate.runtime.v_6_2.internal.legacy;

import org.hibernate.Incubating;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;

public class BooleanType extends AbstractSingleColumnStandardBasicType<Boolean>
		implements AdjustableBasicType<Boolean> {
	public static final BooleanType INSTANCE = new BooleanType();

	public BooleanType() {
		this(BooleanJdbcType.INSTANCE, BooleanJavaType.INSTANCE);
	}

	protected BooleanType(JdbcType jdbcType, BooleanJavaType javaTypeDescriptor) {
		super(jdbcType, javaTypeDescriptor);
	}

	@Incubating
	public BooleanType(JdbcType jdbcType, JavaType<Boolean> javaTypeDescriptor) {
		super(jdbcType, javaTypeDescriptor);
	}

	@Override
	public String getName() {
		return "boolean";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), boolean.class.getName(), Boolean.class.getName() };
	}

}

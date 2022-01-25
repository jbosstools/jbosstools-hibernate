package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ClassJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class ClassType extends AbstractSingleColumnStandardBasicType<Class> {
	public static final ClassType INSTANCE = new ClassType();

	public ClassType() {
		super(VarcharJdbcType.INSTANCE, ClassJavaTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "class";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}

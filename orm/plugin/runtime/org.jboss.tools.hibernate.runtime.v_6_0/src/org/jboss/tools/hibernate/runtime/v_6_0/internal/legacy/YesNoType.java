package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import org.hibernate.metamodel.model.convert.spi.BasicValueConverter;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.ConvertedBasicType;
import org.hibernate.type.YesNoConverter;
import org.hibernate.type.descriptor.java.BooleanJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

public class YesNoType extends AbstractSingleColumnStandardBasicType<Boolean> implements ConvertedBasicType<Boolean> {

	public static final YesNoType INSTANCE = new YesNoType();

	public YesNoType() {
		super(CharJdbcType.INSTANCE, BooleanJavaTypeDescriptor.INSTANCE);
	}

	@Override
	public String getName() {
		return "yes_no";
	}

	@Override
	public BasicValueConverter<Boolean, ?> getValueConverter() {
		return YesNoConverter.INSTANCE;
	}
}

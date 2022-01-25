package org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy;

import java.util.Locale;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.LocaleJavaTypeDescriptor;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class LocaleType extends AbstractSingleColumnStandardBasicType<Locale> {

	public static final LocaleType INSTANCE = new LocaleType();

	public LocaleType() {
		super( VarcharJdbcType.INSTANCE, LocaleJavaTypeDescriptor.INSTANCE );
	}

	public String getName() {
		return "locale";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}

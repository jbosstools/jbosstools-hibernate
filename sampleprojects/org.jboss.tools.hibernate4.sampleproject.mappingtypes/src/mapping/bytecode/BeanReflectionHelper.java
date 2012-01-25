package mapping.bytecode;

import org.hibernate.property.BasicPropertyAccessor;
import org.hibernate.property.Getter;
import org.hibernate.property.Setter;

import java.util.Date;

/**
 * @author Steve Ebersole
 */
public class BeanReflectionHelper {

	public static final Object[] TEST_VALUES = new Object[] {
			"hello", new Long(1), new Integer(1), new Date(), new Long(1), new Integer(1), new Object() //$NON-NLS-1$
	};

	private static final String[] getterNames = new String[7];
	private static final String[] setterNames = new String[7];
	private static final Class[] types = new Class[7];

	static {
		BasicPropertyAccessor propertyAccessor = new BasicPropertyAccessor();
		Getter getter = propertyAccessor.getGetter( Bean.class, "someString" ); //$NON-NLS-1$
		Setter setter = propertyAccessor.getSetter( Bean.class, "someString" ); //$NON-NLS-1$
		getterNames[0] = getter.getMethodName();
		types[0] = getter.getReturnType();
		setterNames[0] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "someLong" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "someLong" ); //$NON-NLS-1$
		getterNames[1] = getter.getMethodName();
		types[1] = getter.getReturnType();
		setterNames[1] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "someInteger" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "someInteger" ); //$NON-NLS-1$
		getterNames[2] = getter.getMethodName();
		types[2] = getter.getReturnType();
		setterNames[2] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "someDate" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "someDate" ); //$NON-NLS-1$
		getterNames[3] = getter.getMethodName();
		types[3] = getter.getReturnType();
		setterNames[3] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "somelong" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "somelong" ); //$NON-NLS-1$
		getterNames[4] = getter.getMethodName();
		types[4] = getter.getReturnType();
		setterNames[4] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "someint" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "someint" ); //$NON-NLS-1$
		getterNames[5] = getter.getMethodName();
		types[5] = getter.getReturnType();
		setterNames[5] = setter.getMethodName();

		getter = propertyAccessor.getGetter( Bean.class, "someObject" ); //$NON-NLS-1$
		setter = propertyAccessor.getSetter( Bean.class, "someObject" ); //$NON-NLS-1$
		getterNames[6] = getter.getMethodName();
		types[6] = getter.getReturnType();
		setterNames[6] = setter.getMethodName();
	}

	public static String[] getGetterNames() {
		return getterNames;
	}

	public static String[] getSetterNames() {
		return setterNames;
	}

	public static Class[] getTypes() {
		return types;
	}
}

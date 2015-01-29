package org.jboss.tools.hibernate.runtime.common.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {
	
	public static Object getInstance(String className, Object caller) {
		Object result = null;
		try {
			result = caller.getClass().getClassLoader().loadClass(className).newInstance();
		} catch (InstantiationException | 
				IllegalAccessException | 
				ClassNotFoundException e) {
			HibernateRuntimeCommon.log(e);
		}
		return result;
	}

	public static Object invokeMethod(
			Object object, 
			String name,
			Class<?>[] parameterTypes, 
			Object[] arguments) {
		Object result = null;
		Method method;
		try {
			method = object.getClass().getMethod(name, parameterTypes);
			result = method.invoke(object, arguments);
		} catch (NoSuchMethodException | 
				SecurityException | 
				IllegalAccessException | 
				IllegalArgumentException | 
				InvocationTargetException e) {
			HibernateRuntimeCommon.log(e);
		}
		return result;
	}

}

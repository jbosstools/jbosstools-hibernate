package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.tools.hibernate.runtime.common.internal.HibernateRuntimeCommon;

public class Util {
	
	public static Class<?> getClass(String className, ClassLoader loader) {
		Class<?> result = null;
		try {
			result = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			HibernateRuntimeCommon.log(e);
		}
		return result;
	}
	
	public static Object getInstance(String className, ClassLoader loader) {
		Object result = null;
		try {
			result = getClass(className, loader).newInstance();
		} catch (InstantiationException | 
				IllegalAccessException e) {
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
			method.setAccessible(true);
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

package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.tools.hibernate.runtime.common.internal.HibernateRuntimeCommon;

public class Util {
	
	public static Class<?> getClass(String className, Object caller) {
		Class<?> result = null;
		try {
			result = caller.getClass().getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			HibernateRuntimeCommon.log(e);
		}
		return result;
	}
	
	public static Object getInstance(String className, Object caller) {
		Object result = null;
		try {
			result = getClass(className, caller).newInstance();
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

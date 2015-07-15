package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.Field;
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
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
		return result;
	}

	public static Object invokeMethod(
			Object object, 
			String name,
			Class<?>[] parameterTypes, 
			Object[] arguments) {
		Object result = null;
		try {
			Method method = object.getClass().getMethod(name, parameterTypes);
			method.setAccessible(true);
			result = method.invoke(object, arguments);
		} catch (NoSuchMethodException | 
				SecurityException | 
				IllegalAccessException | 
				IllegalArgumentException e) {
			HibernateRuntimeCommon.log(e);
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				throw new RuntimeException(t);
			}
		}
		return result;
	}
	
	public static Object invokeMethod(
			Class<?> clazz, 
			String name, 
			Class<?>[] parameterTypes, 
			Object[] arguments) {
		Object result = null;
		try {
			Method method = clazz.getMethod(name, parameterTypes);
			method.setAccessible(true);
			result = method.invoke(null, arguments);
		} catch (NoSuchMethodException | 
				SecurityException | 
				IllegalAccessException | 
				IllegalArgumentException e) {
			HibernateRuntimeCommon.log(e);
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				throw new RuntimeException(t);
			}
		}
		return result;
	}
	
	public static Object getFieldValue(Class<?> clazz, String fieldName, Object object) {
		Object result = null;
		try {
			Field field = clazz.getField(fieldName);
			field.setAccessible(true);
			result = field.get(object);
		} catch (IllegalArgumentException | 
				IllegalAccessException | 
				NoSuchFieldException | 
				SecurityException e) {
			HibernateRuntimeCommon.log(e);
			throw new RuntimeException(e);
		}
		return result;
	}

}

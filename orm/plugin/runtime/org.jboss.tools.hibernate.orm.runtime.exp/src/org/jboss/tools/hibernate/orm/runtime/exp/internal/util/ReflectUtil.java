package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.Method;
import java.util.Map;

public class ReflectUtil {
	
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap =
            Map.of(boolean.class, Boolean.class,
                    byte.class, Byte.class,
                    char.class, Character.class,
                    double.class, Double.class,
                    float.class, Float.class,
                    int.class, Integer.class,
                    long.class, Long.class,
                    short.class, Short.class);

	public static Method lookupMethod(Class<?> methodClass, String methodName, Class<?>[] argumentClasses) {
		Method result = null;
		Method[] methods = methodClass.getMethods();
		for (Method candidate : methods) {
			if (!methodName.equals(candidate.getName())) continue;
			int parameterCount = candidate.getParameterCount();
			if (argumentClasses == null && parameterCount == 0) {
				result = candidate;
				break;
			}
			else if ((argumentClasses == null && parameterCount != 0) || argumentClasses.length != parameterCount) {
				continue;
			}
			result = candidate;		
			Class<?>[] parameterTypes = candidate.getParameterTypes();
			for (int i = 0; i < argumentClasses.length; i++) {
				if (!isAssignableTo(argumentClasses[i], parameterTypes[i])) {
					result = null;
					break;
				}
			}
			if (result != null) break;
		}
		if (result != null) {
			result.setAccessible(true);
		}
		return result;
	}
	
    private static boolean isPrimitiveWrapperOf(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("First argument has to be primitive type");
        }
        return primitiveWrapperMap.get(primitive) == targetClass;
    }

    private static boolean isAssignableTo(Class<?> from, Class<?> to) {
        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (from.isPrimitive()) {
            return isPrimitiveWrapperOf(to, from);
        }
        if (to.isPrimitive()) {
            return isPrimitiveWrapperOf(from, to);
        }
        return false;
    }

	

}

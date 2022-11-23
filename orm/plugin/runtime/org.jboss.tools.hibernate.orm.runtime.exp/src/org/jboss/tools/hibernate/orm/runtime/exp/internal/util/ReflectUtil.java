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
			else if (argumentClasses == null && parameterCount != 1) {
				continue;
			} 
			else if (argumentClasses == null && parameterCount == 1) {
				Class<?>[] parameterTypes = candidate.getParameterTypes();
				if (parameterTypes[0].isArray()) {
					result = candidate;
					break;
				}
				continue;
			}
			result = candidate;		
			if (parameterCount > 0) {
				Class<?>[] parameterTypes = candidate.getParameterTypes();
				for (int i = 0; i < parameterTypes.length - 1; i++) {
					if (!isAssignableTo(argumentClasses[i], parameterTypes[i])) {
						result = null;
						break;
					}
				}
				if (parameterTypes[parameterTypes.length - 1].isArray()) {
					Class<?> componentType = parameterTypes[parameterTypes.length - 1].getComponentType();
					for (int i = parameterTypes.length - 1; i < argumentClasses.length; i++) {
						if (!isAssignableTo(argumentClasses[i], componentType)) {
							result = null;
							break;
						}
					}
				} else {
					if (!isAssignableTo(
							argumentClasses[parameterTypes.length - 1], 
							parameterTypes[parameterTypes.length - 1])) {
						result = null;
					}
				}
			}
			if (result != null) break;
		}
		if (result != null) {
			result.setAccessible(true);
		}
		return result;
	}
	
	public static Object invokeMethod(Method method, Object object, Object[] args) throws Exception {
		int parameterCount = method.getParameterCount();
		int argumentCount = args == null ? 0 : args.length;
		Object[] arguments = new Object[parameterCount];
		if (parameterCount > 0) {
			for (int i = 0; i < parameterCount - 1; i++) {
				arguments[i] = args[i];
			}
			if (method.getParameters()[parameterCount - 1].isVarArgs()) {
		 		if (parameterCount < argumentCount) {
		 			Object[] varArgs = new Object[argumentCount - parameterCount];
		 			for (int i = 0; i < varArgs.length; i++) {
		 				varArgs[i] = args[parameterCount - 1 + i];
		 			}
		 			arguments[parameterCount - 1] = varArgs;
		 		} else if (parameterCount == argumentCount + 1) {
		 			arguments[parameterCount - 1] = new Object[] {};
		 		}
			} else {
				arguments[parameterCount - 1] = args[parameterCount - 1];
			}
		}
		return method.invoke(object, arguments);
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

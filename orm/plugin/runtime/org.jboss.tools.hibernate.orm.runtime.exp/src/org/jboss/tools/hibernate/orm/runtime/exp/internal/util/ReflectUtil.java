package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		return selectMethodByComparingArgumentClasses(
				collectNamedMethodsByParameterCount(methodName, methodClass), 
				argumentClasses);
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
    
    private static Map<Integer, List<Method>> collectNamedMethodsByParameterCount(
    		String name, 
    		Class<?> methodClass) {
    	HashMap<Integer, List<Method>> result = new HashMap<Integer, List<Method>>();
    	for (Method m : methodClass.getMethods()) {
    		if (name.equals(m.getName())) {
    			int parameterCount = m.getParameterCount();
    			List<Method> l = result.get(parameterCount);
    			if (l == null) {
    				l = new ArrayList<Method>();
    				result.put(parameterCount, l);
    			}
    			l.add(m);
    		}
    	}
    	return result;
    }

	private static Method selectMethodByComparingArgumentClasses(
			Map<Integer, List<Method>> methods, 
			Class<?>[] argumentClasses) {
		Method result = null;
		Class<?>[] actualArgumentClasses = argumentClasses == null ? new Class<?>[] {} : argumentClasses;
		for (int i = 0; i <= actualArgumentClasses.length + 1; i++) {
			List<Method> candidates = methods.get(i);
			if (candidates != null) {
				result = tryMethodsByParameterCount(methods.get(i), actualArgumentClasses);				
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}
	
	private static Method tryMethodsByParameterCount(
			List<Method> candidates, 
			Class<?>[] actualArgumentClasses) {
		Method result = null;
		for (Method m : candidates) {
			result = m;
			Parameter[] parameters = m.getParameters();
			int maxAmount = Integer.min(actualArgumentClasses.length, parameters.length - 1);
			for (int i = 0; i < maxAmount; i++) {
				if (!isAssignableTo(actualArgumentClasses[i], parameters[i].getType())) {
					result = null;
					break;
				}
			}
			if (actualArgumentClasses.length < parameters.length) {
				if (!parameters[parameters.length - 1].isVarArgs()) {
					result = null;
				}
			} else if (actualArgumentClasses.length == parameters.length) {
				if (actualArgumentClasses.length > 0) {
					Class<?> left = actualArgumentClasses[actualArgumentClasses.length - 1];
					Class<?> right = null;
					if (parameters[parameters.length - 1].isVarArgs()) {
						right = parameters[parameters.length - 1].getType().getComponentType();
					} else {
						right = parameters[parameters.length - 1].getType();
					}
					if (!isAssignableTo(left, right)) {
						result = null;
					}
				}
			} else {
				if (parameters.length == 0 || !parameters[parameters.length - 1].isVarArgs()) {
					result = null;
				} else {
					for (int i = parameters.length - 1; i < actualArgumentClasses.length; i++) {
						if (!isAssignableTo(
								actualArgumentClasses[i], 
								parameters[parameters.length - 1].getType())) {
							result = null;
							break;
						}
					}
				}
			}
			if (result != null) {
				result.setAccessible(true);
				break;
			}
		}
		return result;
	}
	
}

package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

public class GenericFacadeFactory {
	
	public static IFacade createFacade(Class<?> facadeClass, Object target) {
		return (IFacade)Proxy.newProxyInstance(
					GenericFacadeFactory.class.getClassLoader(), 
					new Class[] { facadeClass, IFacade.class }, 
					new FacadeInvocationHandler(target));
	}
	
	private static Class<?>[] constructArgumentClasses(Object[] args) {
		Class<?>[] result = null;
		if (args != null) {
			result = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				Class<?> argClass = args[i].getClass();
				if (IFacade.class.isAssignableFrom(argClass)) {
					argClass = ((IFacade)args[i]).getTarget().getClass();
				}
				result[i] = argClass;
			}
		}
		return result;
	}
	
	private static Object[] unwrapFacades(Object[] args) {
		Object[] result = null;
		if (args != null) {
			result = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				if (IFacade.class.isAssignableFrom(args[i].getClass())) {
					result[i] = ((IFacade)args[i]).getTarget();
				} else {
					result[i] = args[i];
				}
			}
		}
		return result;
 	}
	
	
	private static class FacadeInvocationHandler implements InvocationHandler {
		
		private Object target = null;
		
		private FacadeInvocationHandler(Object target) {
			FacadeInvocationHandler.this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = null;
			if ("getTarget".equals(method.getName())) {
				return result = target;
			} else {
				Method targetMethod = lookupMethod(
						target.getClass(), 
						method.getName(), 
						constructArgumentClasses(args));
				if (targetMethod != null) {
					result = targetMethod.invoke(target, unwrapFacades(args));
					Class<?> returnedClass = method.getReturnType();
					if (classesSet.contains(returnedClass)) {
						result = createFacade(returnedClass, result);
					} 
				}
			}
			return result;
		}
		
	}
	
	private static Method lookupMethod(Class<?> methodClass, String methodName, Class<?>[] argumentClasses) {
		Method result = null;
		Method[] methods = methodClass.getMethods();
		for (Method candidate : methods) {
			if (!methodName.equals(candidate.getName())) continue;
			int parameterCount = candidate.getParameterCount();
			if (argumentClasses == null && parameterCount == 0) {
				result = candidate;
				break;
			}
			else if (argumentClasses.length != parameterCount) {
				continue;
			}
			result = candidate;		
			Class<?>[] parameterTypes = candidate.getParameterTypes();
			for (int i = 0; i < argumentClasses.length; i++) {
				if (!parameterTypes[i].isAssignableFrom(argumentClasses[i])) {
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
	
	private static Set<Class<?>> classesSet = new HashSet<>(
			Arrays.asList(new Class[] {
					IReverseEngineeringStrategy.class
			}));
	

}

package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class GenericFacadeFactory {
	
	public static IFacade createFacade(Class<?> facadeClass, Object target) {
		return (IFacade)Proxy.newProxyInstance(
					GenericFacadeFactory.class.getClassLoader(), 
					new Class[] { facadeClass, IFacade.class }, 
					new FacadeInvocationHandler(facadeClass, target));
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
	
	private static class FacadeInvocationHandler implements InvocationHandler {
		
		private Object target = null;
		
		private FacadeInvocationHandler(Class<?> facadeClass, Object target) {
			FacadeInvocationHandler.this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = null;
			if ("getTarget".equals(method.getName())) {
				result = target;
			} else {
				Method targetMethod = ReflectUtil.lookupMethod(
						target.getClass(), 
						method.getName(), 
						constructArgumentClasses(args));
				if (targetMethod != null) {
					result = targetMethod.invoke(target, unwrapFacades(args));
					Class<?> returnedClass = method.getReturnType();
					if (Iterator.class.isAssignableFrom(returnedClass) ) {
						result = createIteratorResult(
								(Iterator<?>)result, 
								determineActualIteratorParameterType(method.getGenericReturnType()));
					}
					else if (classesSet.contains(returnedClass)) {
						if (result == target) {
							result = proxy;
						} else {
							result = createFacade(returnedClass, result);
						}
					} 
				}
			}
			return result;
		}
		
	}
	
	private static Class<?> determineActualIteratorParameterType(Type type) {
		Class<?> result = Object.class;
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;
			result = (Class<?>) parameterizedType.getActualTypeArguments()[0];
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
	
	private static Iterator<?> createIteratorResult(Iterator<?> targetIterator, Class<?> actualType) {
		boolean actualTypeIsFacade = classesSet.contains(actualType);
		return new Iterator<Object>() {
			@Override
			public boolean hasNext() {
				return targetIterator.hasNext();
			}
			@Override
			public Object next() {
				Object result = targetIterator.next();
				if (actualTypeIsFacade) result = createFacade(actualType, result);
				return result;
			}
			
		};		
	}	
	
	private static Set<Class<?>> classesSet = new HashSet<>(
			Arrays.asList(new Class[] {
					IConfiguration.class,
					IPersistentClass.class,
					IReverseEngineeringStrategy.class,
					IReverseEngineeringSettings.class,
					ISessionFactory.class
			}));
	
}

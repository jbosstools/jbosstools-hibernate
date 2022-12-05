package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class GenericFacadeFactory {
	
	public static IFacade createFacade(Class<?> facadeClass, Object target) {
		return (IFacade)Proxy.newProxyInstance(
					GenericFacadeFactory.class.getClassLoader(), 
					new Class[] { facadeClass, IFacade.class }, 
					new FacadeInvocationHandler(facadeClass, target));
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
				Class<?>[] argumentClasses = argumentClasses(args);
				Method targetMethod = ReflectUtil.lookupMethod(
						target.getClass(), 
						method.getName(), 
						argumentClasses);
				if (targetMethod != null) {
					try {
						result = ReflectUtil.invokeMethod(targetMethod, target, unwrapFacades(args));
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
					if (result != null) {
						Class<?> returnedClass = method.getReturnType();
						Type genericReturnType = method.getGenericReturnType();
						if (genericReturnType != null && genericReturnType instanceof ParameterizedType) {
							if (Iterator.class.isAssignableFrom(returnedClass) ) {
								result = createIteratorResult(
										(Iterator<?>)result, 
										(ParameterizedType)genericReturnType);
							}
							else if (Map.class.isAssignableFrom(returnedClass)) {
								result = createMapResult(
										(Map<?,?>)result,
										(ParameterizedType)genericReturnType);
							}
						}
						else if (returnedClass.isArray() && classesSet.contains(returnedClass.getComponentType())) {
							result = createArrayResult(
									(Object[])result,
									returnedClass.getComponentType());

						}
						else if (classesSet.contains(returnedClass)) {
							if (result == target) {
								result = proxy;
							} else {
								result = createFacade(returnedClass, result);
							}
						} 
					}
				} else {
					throw new RuntimeException(
							"Method '" + 
							target.getClass().getName() + "#" +
							method.getName() + 
							parameterTypes(argumentClasses) +
							" cannot be found.");
							
				}
			}
			return result;
		}
		
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
	
	private static Iterator<?> createIteratorResult(Iterator<?> targetIterator, ParameterizedType parameterizedType) {
		Class<?> actualType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
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
	
	private static Map<?, ?> createMapResult(Map<?, ?> map, ParameterizedType parameterizedType) {
		Map<Object, Object> result = (Map<Object, Object>)map;
		Class<?> actualValueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
		if  (classesSet.contains(actualValueType)) {
			for (Object key : map.keySet()) {
				result.put(key, createFacade(actualValueType, result));
			}
		}
		return result;
	}
	
	private static <T> T[] createArrayResult(Object[] array, Class<?> actualType) {
		T[] result = (T[])Array.newInstance(actualType, array.length);
		for (int i = 0; i < array.length; i++) {
			result[i] = (T)createFacade(actualType, array[i]);
		}
		return result;
	}
	
	private static Class<?>[] argumentClasses(Object[] args) {
		Class<?>[] result = new Class<?>[0];
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
	
	private static String parameterTypes(Class<?>[] classes) {
		StringBuffer sb = new StringBuffer("(");
		for (Class<?> c : classes) {
			sb.append(c.getSimpleName()).append(",");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")");
		return sb.toString();
	}
	
	private static Set<Class<?>> classesSet = new HashSet<>(
			Arrays.asList(new Class[] {
					IClassMetadata.class,
					ICollectionMetadata.class,
					IConfiguration.class,
					INamingStrategy.class,
					IPersistentClass.class,
					IReverseEngineeringStrategy.class,
					IReverseEngineeringSettings.class,
					ISession.class,
					ISessionFactory.class,
					ITable.class,
					IType.class,
					IValue.class
			}));
	
}

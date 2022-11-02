package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.tools.hibernate.runtime.common.IFacade;

public class GenericFacadeFactory {
	
	public static IFacade createFacade(Class<?> facadeClass, Object target) {
		return (IFacade)Proxy.newProxyInstance(
					GenericFacadeFactory.class.getClassLoader(), 
					new Class[] { facadeClass, IFacade.class }, 
					new InvocationHandler() {						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if ("getTarget".equals(method.getName())) {
								return target;
							} else {
								Method targetMethod = target.getClass().getMethod(
										method.getName(), 
										constructArgumentClasses(args));
								return targetMethod.invoke(target, args);
							}
						}
					});
	}
	
	private static Class<?>[] constructArgumentClasses(Object[] args) {
		Class<?>[] result = null;
		if (args != null) {
			result = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				result[i] = args[i].getClass();
			}
		}
		return result;
	}
	

}

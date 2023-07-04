package org.jboss.tools.hibernate.runtime.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public interface IHQLCompletionHandler {

	boolean accept(IHQLCompletionProposal proposal);
	void completionFailure(String errorMessage);
	
	default boolean accept(Object proposal) {
		IHQLCompletionProposal newProposal = (IHQLCompletionProposal)Proxy.newProxyInstance(
				IHQLCompletionHandler.class.getClassLoader(), 
				new Class[] { IHQLCompletionProposal.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Object result = proposal.getClass().getMethod(method.getName(), new Class[] {})
								.invoke(proposal);
						return wrapIfNeeded(method, result);
					}
				});
		return accept(newProposal);
	}
	
	default Object wrapIfNeeded(Method calledMethod, Object target) {
		Class<?> returnClass = calledMethod.getReturnType();
		if (getFacadeClasses().contains(calledMethod.getReturnType())) {
			return Proxy.newProxyInstance(
					IHQLCompletionProposal.class.getClassLoader(), 
					new Class[] { returnClass }, 
					new InvocationHandler() {						
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							try {
								Method methodToCall = target.getClass().getMethod(method.getName(), new Class[] {});
								if (methodToCall != null) {
									return wrapIfNeeded(method, methodToCall.invoke(target));
								}
							} catch (NoSuchMethodException e) {
								return bestGuessResult(method);
							}
							return null;
						}
					});
		} else {
			return target;
		}
	}
	
	default List<Class<?>> getFacadeClasses() {
		return Arrays.asList(IProperty.class, IPersistentClass.class, IValue.class );
	}
	
	default Object bestGuessResult(Method m) {
		if (boolean.class.isAssignableFrom(m.getReturnType())) return false;
		return null;
	}
	
}

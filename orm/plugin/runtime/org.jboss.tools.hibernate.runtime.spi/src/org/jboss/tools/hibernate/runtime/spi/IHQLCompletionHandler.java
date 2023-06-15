package org.jboss.tools.hibernate.runtime.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
						return proposal.getClass().getMethod(method.getName(), new Class[] {})
								.invoke(proposal);
					}
				});
		return accept(newProposal);
	}
	
}

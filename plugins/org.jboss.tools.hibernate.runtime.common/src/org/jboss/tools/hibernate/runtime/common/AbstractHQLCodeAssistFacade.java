package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;

public abstract class AbstractHQLCodeAssistFacade 
extends AbstractFacade 
implements IHQLCodeAssist {

	public AbstractHQLCodeAssistFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler) {
		Util.invokeMethod(
				getTarget(), 
				"codeComplete", 
				new Class[] { String.class, int.class, getIHQLCompletionRequestorClass() }, 
				new Object[] { query, currentOffset, createIHQLCompletionRequestor(handler)});
	}
	
	protected Object createIHQLCompletionRequestor(IHQLCompletionHandler handler) {
		return Proxy.newProxyInstance(
				getFacadeFactoryClassLoader(), 
				new Class[] { getIHQLCompletionRequestorClass() }, 
				new HQLCompletionRequestorInvocationHandler(handler));
	}
	
	protected Class<?> getIHQLCompletionRequestorClass() {
		return Util.getClass(
				getIHQLCompletionRequestorClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getIHQLCompletionRequestorClassName() {
		return "org.hibernate.tool.ide.completion.IHQLCompletionRequestor";
	}
	
	protected class HQLCompletionRequestorInvocationHandler 
	implements InvocationHandler {		
		private IHQLCompletionHandler handler = null;
		public HQLCompletionRequestorInvocationHandler(IHQLCompletionHandler handler) {
			this.handler = handler;
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = null;
			String methodName = method.getName();
			if ("accept".equals(methodName)) {
				result = handler.accept(
						getFacadeFactory().createHQLCompletionProposal(args[0]));
			} else if ("completionFailure".equals(methodName)) {
				handler.completionFailure((String)args[0]);
			}
			return result;
		}		
	}

}

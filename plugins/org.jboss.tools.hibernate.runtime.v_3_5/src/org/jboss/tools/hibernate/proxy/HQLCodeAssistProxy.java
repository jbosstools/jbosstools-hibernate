package org.jboss.tools.hibernate.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCodeAssistFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;

public class HQLCodeAssistProxy extends AbstractHQLCodeAssistFacade {
	
	public HQLCodeAssistProxy(
			IFacadeFactory facadeFactory,
			HQLCodeAssist hqlCodeAssist) {
		super(facadeFactory, hqlCodeAssist);
	}
	
	public HQLCodeAssist getTarget() {
		return (HQLCodeAssist)super.getTarget();
	}

	@Override
	public void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler) {
		getTarget().codeComplete(
				query, 
				currentOffset, 
				createIHQLCompletionRequestor(handler));
	}
	
	private IHQLCompletionRequestor createIHQLCompletionRequestor(IHQLCompletionHandler handler) {
		return (IHQLCompletionRequestor)Proxy.newProxyInstance(
				getFacadeFactoryClassLoader(), 
				new Class[] { IHQLCompletionRequestor.class }, 
				new HQLCompletionRequestorInvocationHandler(handler));
	}
	
	private class HQLCompletionRequestorInvocationHandler 
	implements InvocationHandler {		
		private HQLCompletionRequestor requestor;		
		public HQLCompletionRequestorInvocationHandler(IHQLCompletionHandler handler) {
			requestor = new HQLCompletionRequestor(handler);
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object result = null;
			String methodName = method.getName();
			if ("accept".equals(methodName)) {
				result = method.invoke(requestor, args);
			} else if ("completionFailure".equals(methodName)) {
				result = method.invoke(requestor, args);
			}
			return result;
		}		
	}
	
	private class HQLCompletionRequestor implements IHQLCompletionRequestor {		
		private IHQLCompletionHandler handler = null;		
		public HQLCompletionRequestor(IHQLCompletionHandler handler) {
			this.handler = handler;
		}
		@Override
		public boolean accept(HQLCompletionProposal proposal) {
			return handler.accept(
				HQLCodeAssistProxy.this.getFacadeFactory().createHQLCompletionProposal(proposal));
		}
		@Override
		public void completionFailure(String errorMessage) {
			handler.completionFailure(errorMessage);			
		}		
	}
	
}

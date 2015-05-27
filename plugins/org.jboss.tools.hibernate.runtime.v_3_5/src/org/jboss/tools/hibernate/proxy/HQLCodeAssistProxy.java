package org.jboss.tools.hibernate.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
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

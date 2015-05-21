package org.jboss.tools.hibernate.runtime.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;

public abstract class AbstractJDBCReaderFacade 
extends AbstractFacade 
implements IJDBCReader {

	public AbstractJDBCReaderFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	public Object createProgressListener(IProgressListener progressListener) {
		return Proxy.newProxyInstance(
				getFacadeFactoryClassLoader(), 
				new Class[] { getProgressListenerClass() }, 
				new ProgressListenerInvocationHandler(progressListener));
	}
	
	public Class<?> getProgressListenerClass() {
		return Util.getClass(
				getProgressListenerClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	public String getProgressListenerClassName() {
		return "org.hibernate.cfg.reveng.ProgressListener";
	}
	
	public class ProgressListenerInvocationHandler implements InvocationHandler {		
		public Object target;		
		public ProgressListenerInvocationHandler(Object target) {
			this.target = target;
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("startSubTask".equals(method.getName())) {
				Util.invokeMethod(target, "startSubTask", new Class[] { String.class }, args);
			}
			return null;
		}		
	}
		
}

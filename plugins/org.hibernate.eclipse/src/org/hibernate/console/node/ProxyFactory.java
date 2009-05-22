/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.console.node;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContextHolder;

public class ProxyFactory {

	private static final String GET_EXECUTION_CONTEXT = "getExecutionContext"; //$NON-NLS-1$

	private static final MethodInterceptor EXECUTIONCONTEXTHOLDER_METHOD_INTERCEPTOR = new MethodInterceptor() {
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			ExecutionContextHolder bn = (ExecutionContextHolder)obj;
			ExecutionContext executionContext = bn.getExecutionContext();
			try {
				executionContext.installLoader();
				return proxy.invokeSuper(obj, args);
			} finally {
				executionContext.uninstallLoader();
			}
			
		}
	};
	
	private static final CallbackFilter baseNodeFilter = new CallbackFilter() {
		public int accept(Method method) {
			Class<?> declaringClass = method.getDeclaringClass();
			if(ExecutionContextHolder.class.isAssignableFrom(declaringClass ) ) {
				if(GET_EXECUTION_CONTEXT.equals(method.getName() ) ) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 1;
			}
		}
	};
	
	public static Enhancer createEnhancer(Class<?> clazz) {
		Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setCallbacks(new Callback[] { ProxyFactory.EXECUTIONCONTEXTHOLDER_METHOD_INTERCEPTOR, NoOp.INSTANCE });
        e.setCallbackFilter(ProxyFactory.baseNodeFilter);
        return e;
	}
	
	static class Tracker implements MethodInterceptor {

		ExecutionContextHolder ech;
		Object real;
		
		Tracker(ExecutionContextHolder ech, Object real) {
			this.ech = ech;
			this.real = real;
		}
		
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			if(GET_EXECUTION_CONTEXT.equals(method.getName())) { 
				return ech.getExecutionContext(); 
			} else {
				return method.invoke(real,args); 
			}
		}
		
	}
	
	public static Object createEnhancedObject(Class<?> clazz, Object realObject, ExecutionContextHolder ech) {
		Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setInterfaces(new Class[] { ExecutionContextHolder.class } );
        e.setCallback(new Tracker(ech, realObject));
        //e.setCallbackFilter(ProxyFactory.baseNodeFilter);
   
        return e.create();
	}
}

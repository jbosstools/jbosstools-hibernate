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

	private static final String GET_EXECUTION_CONTEXT = "getExecutionContext";

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
			Class declaringClass = method.getDeclaringClass();
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

		public boolean equals(Object o) {
			return o==this;
		}
	};
	
	public static Enhancer createEnhancer(Class clazz) {
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
	
	public static Object createEnhancedObject(Class clazz, Object realObject, ExecutionContextHolder ech) {
		Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setInterfaces(new Class[] { ExecutionContextHolder.class } );
        e.setCallback(new Tracker(ech, realObject));
        //e.setCallbackFilter(ProxyFactory.baseNodeFilter);
   
        return e.create();
	}
}

package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.jboss.tools.hibernate.runtime.common.IFacade;

public class GenericFacadeUtil {
	
	public static void setTarget(IFacade genericFacade, Object target) {
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(genericFacade);
			Field field = invocationHandler.getClass().getDeclaredField("target");
			field.setAccessible(true);
			field.set(invocationHandler, target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.internal.export.java.POJOClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPOJOClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.Before;
import org.junit.Test;

public class PojoClassFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final String QUALIFIED_DECLARATION_NAME = new String("foobar");
	
	private IPOJOClass pojoClassFacade = null; 
	private POJOClass pojoClassTarget = null;
	
	@Before
	public void before() {
		pojoClassTarget = createPojoClass();
		pojoClassFacade = new AbstractPOJOClassFacade(FACADE_FACTORY, pojoClassTarget) {};
	}
	
	@Test
	public void testGetQualifiedDeclarationName() {
		assertSame(QUALIFIED_DECLARATION_NAME, pojoClassFacade.getQualifiedDeclarationName());
	}
	
	private POJOClass createPojoClass() {
		return (POJOClass)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { POJOClass.class }, 
				new InvocationHandler() {		
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("getQualifiedDeclarationName".equals(method.getName())) {
							return QUALIFIED_DECLARATION_NAME;
						}
						return null;
					}
				});
	}
	
}

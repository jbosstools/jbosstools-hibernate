package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;


public class Hbm2DDLExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter hbm2DDLExporterFacade = null; 
	private Hbm2DDLExporter hbm2ddlExporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(Hbm2DDLExporter.class);
		Class<?> proxyClass = proxyFactory.createClass();
		hbm2ddlExporter = (Hbm2DDLExporter)proxyClass.newInstance();
		((ProxyObject)hbm2ddlExporter).setHandler(new MethodHandler() {		
			@Override
			public Object invoke(
					Object self, 
					Method m, 
					Method proceed, 
					Object[] args) throws Throwable {
				if (methodName == null) {
					methodName = m.getName();
				}
				if (arguments == null) {
					arguments = args;
				}
				return proceed.invoke(self, args);
			}
		});
		hbm2DDLExporterFacade = new AbstractHbm2DDLExporterFacade(FACADE_FACTORY, hbm2ddlExporter) {};
		reset();
	}
	
	@Test
	public void testSetExport() {
		hbm2DDLExporterFacade.setExport(true);
		Assert.assertEquals("setExport", methodName);
		Assert.assertArrayEquals(new Object[] { true }, arguments);
	}
	
	@Test
	public void testGetProperties() {
		Hashtable<Object, Object> first = new Properties();
		hbm2ddlExporter.setProperties((Properties)first);
		reset();
		Hashtable<Object, Object> second = hbm2DDLExporterFacade.getProperties();
		Assert.assertEquals("getProperties", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
		Assert.assertSame(first, second);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;


public class Hbm2DDLExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter hbm2DDLExporterFacade = null; 
	private Hbm2DDLExporter hbm2ddlExporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
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
		assertEquals("setExport", methodName);
		assertArrayEquals(new Object[] { true }, arguments);
	}
	
	@Test
	public void testGetProperties() {
		hbm2DDLExporterFacade.getProperties();
		assertEquals("getProperties", methodName);
		assertArrayEquals(new Object[] {}, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

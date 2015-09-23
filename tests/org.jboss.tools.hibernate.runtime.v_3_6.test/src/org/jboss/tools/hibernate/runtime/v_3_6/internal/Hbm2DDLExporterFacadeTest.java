package org.jboss.tools.hibernate.runtime.v_3_6.internal;

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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


public class Hbm2DDLExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter hbm2DDLExporterFacade = null; 
	private Hbm2DDLExporter hbm2ddlExporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Hbm2DDLExporter.class);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(
					Object obj, 
					Method method, 
					Object[] args, 
					MethodProxy proxy) throws Throwable {
				if (methodName == null) {
					methodName = method.getName();
				}
				if (arguments == null) {
					arguments = args;
				}
				return proxy.invokeSuper(obj, args);
			}					
		});
		hbm2ddlExporter = (Hbm2DDLExporter)enhancer.create();
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

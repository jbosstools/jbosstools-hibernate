package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.Method;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


public class HibernateMappingExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(HibernateMappingExporter.class);
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
				if ("start".equals(methodName)) {
					return null;
				} else {
					return proxy.invokeSuper(obj, args);
				}
			}					
		});
		hibernateMappingExporterFacade = new AbstractHibernateMappingExporterFacade(
				FACADE_FACTORY, 
				enhancer.create()) {};
		reset();
	}
	
	@Test
	public void testStart() {
		hibernateMappingExporterFacade.start();
		Assert.assertEquals("start", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

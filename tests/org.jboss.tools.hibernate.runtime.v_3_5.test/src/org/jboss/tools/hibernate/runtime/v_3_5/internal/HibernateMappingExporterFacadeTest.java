package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
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
				return proxy.invokeSuper(obj, args);
			}					
		});
		hibernateMappingExporterFacade = new AbstractHibernateMappingExporterFacade(
				FACADE_FACTORY, 
				enhancer.create()) {};
		reset();
	}
	
	@Test
	public void testSetGlobalSettings() {
		final HibernateMappingGlobalSettings hibernateMappingGlobalSettings = new HibernateMappingGlobalSettings();
		IHibernateMappingGlobalSettings hibernateMappingGlobalSettingsFacade = 
				(IHibernateMappingGlobalSettings)Proxy.newProxyInstance(
						FACADE_FACTORY.getClassLoader(), 
						new Class[] { IHibernateMappingGlobalSettings.class, IFacade.class }, 
						new InvocationHandler() {
							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								if ("getTarget".equals(method.getName())) {
									return hibernateMappingGlobalSettings;
								} else {
									return null;
								}
							}						
						} );
		hibernateMappingExporterFacade.setGlobalSettings(hibernateMappingGlobalSettingsFacade);
		Assert.assertEquals("setGlobalSettings", methodName);
		Assert.assertArrayEquals(new Object[] { hibernateMappingGlobalSettings }, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

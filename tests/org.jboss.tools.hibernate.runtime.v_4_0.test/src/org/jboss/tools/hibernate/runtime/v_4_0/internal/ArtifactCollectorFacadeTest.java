package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.lang.reflect.Method;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ArtifactCollectorFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private String methodName = null;
	private Object[] arguments = null;
	
	private IArtifactCollector artifactCollector = null; 
	
	@Before
	public void setUp() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(ArtifactCollector.class);
		Class<?> proxyClass = proxyFactory.createClass();
		ProxyObject proxy = (ProxyObject)proxyClass.newInstance();
		proxy.setHandler(new MethodHandler() {		
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
		artifactCollector = new AbstractArtifactCollectorFacade(FACADE_FACTORY, (ArtifactCollector)proxy) {};
		reset();
	}

	@Test
	public void testGetFileTypes() {
		Assert.assertNotNull(artifactCollector.getFileTypes());
		Assert.assertEquals("getFileTypes", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}

	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ArtifactCollectorFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private String methodName = null;
	private Object[] arguments = null;
	
	private IArtifactCollector artifactCollector = null; 
	
	@BeforeEach
	public void beforeEach() throws Exception {
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
		assertNotNull(artifactCollector.getFileTypes());
		assertEquals("getFileTypes", methodName);
		assertArrayEquals(new Object[] {}, arguments);
	}

	@Test
	public void testFormatFiles() {
		artifactCollector.formatFiles();
		assertEquals("formatFiles", methodName);
		assertArrayEquals(new Object[] {}, arguments);
	}

	@Test
	public void testGetFiles() {
		assertNotNull(artifactCollector.getFiles("foobar"));
		assertEquals("getFiles", methodName);
		assertArrayEquals(new Object[] { "foobar" }, arguments);
	}

	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

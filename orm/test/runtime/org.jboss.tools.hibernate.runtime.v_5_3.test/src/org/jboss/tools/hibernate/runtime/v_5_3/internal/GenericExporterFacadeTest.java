package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractGenericExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;


public class GenericExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IGenericExporter genericExporterFacade = null; 
	private GenericExporter genericExporter = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(GenericExporter.class);
		Class<?> proxyClass = proxyFactory.createClass();
		genericExporter = (GenericExporter)proxyClass.newInstance();
		((ProxyObject)genericExporter).setHandler(new MethodHandler() {		
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
		genericExporterFacade = new AbstractGenericExporterFacade(FACADE_FACTORY, genericExporter) {};
		reset();
	}
	
	@Test
	public void testSetFilePattern() {
		genericExporter.setFilePattern("barfoo");
		assertEquals("barfoo", genericExporter.getFilePattern());
		reset();
		genericExporterFacade.setFilePattern("foobar");
		assertEquals("setFilePattern", methodName);
		assertArrayEquals(new Object[] { "foobar" }, arguments);
		assertEquals("foobar", genericExporter.getFilePattern());
	}
	
	@Test
	public void testSetTemplate() {
		genericExporter.setTemplateName("barfoo");
		assertEquals("barfoo", genericExporter.getTemplateName());
		reset();
		genericExporterFacade.setTemplateName("foobar");
		assertEquals("setTemplateName", methodName);
		assertArrayEquals(new Object[] { "foobar" }, arguments);
		assertEquals("foobar", genericExporter.getTemplateName());
	}
	
	@Test
	public void testSetForEach() {
		genericExporterFacade.setForEach("foobar");
		assertEquals("setForEach", methodName);
		assertArrayEquals(new Object[] { "foobar" }, arguments);
	}
	
	@Test
	public void testGetFilePattern() {
		genericExporter.setFilePattern("foobar");
		reset();
		assertEquals("foobar", genericExporterFacade.getFilePattern());
		assertEquals("getFilePattern", methodName);
		assertArrayEquals(new Object[] {}, arguments);
	}
	
	@Test
	public void testGetTemplateName() {
		genericExporter.setTemplateName("foobar");
		reset();
		assertEquals("foobar", genericExporterFacade.getTemplateName());
		assertEquals("getTemplateName", methodName);
		assertArrayEquals(new Object[] {}, arguments);
	}
	
	private void reset() {
		methodName = null;
		arguments = null;
	}
	
}

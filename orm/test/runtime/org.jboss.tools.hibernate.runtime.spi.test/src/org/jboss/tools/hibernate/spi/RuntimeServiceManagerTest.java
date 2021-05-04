package org.jboss.tools.hibernate.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.jboss.tools.hibernate.spi.internal.TestService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RuntimeServiceManagerTest {
	
	private RuntimeServiceManager runtimeServiceManager = null;
	
	@Before
	public void before() throws Exception {
		Constructor<RuntimeServiceManager> constructor = 
				RuntimeServiceManager.class.getDeclaredConstructor(new Class[] {});
		constructor.setAccessible(true);
		runtimeServiceManager = constructor.newInstance(new Object[] {});
	}

	@Test
	public void testGetInstance() throws Exception {
		Field instanceField = RuntimeServiceManager.class.getDeclaredField("INSTANCE");
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		Assert.assertNotNull(instance);
		Assert.assertSame(instance, RuntimeServiceManager.getInstance());
	}
	
	@Test
	public void testGetDefaultService() {
		IService service = runtimeServiceManager.getDefaultService();
		Assert.assertSame(runtimeServiceManager.findService("0.0.0.Test"), service);
	}
	
	@Test
	public void testGetAllVersions() {
		String[] allVersions = runtimeServiceManager.getAllVersions();
		Assert.assertEquals(1, allVersions.length);
		Assert.assertEquals("0.0.0.Test", allVersions[0]);
	}
	
	@Test
	public void testGetDefaultVersion() throws Exception {
		Assert.assertEquals("0.0.0.Test", runtimeServiceManager.getDefaultVersion());
	}
	
	@Test
	public void testFindService() {
		IService service = runtimeServiceManager.findService("0.0.0.Test");
		Assert.assertNotNull(service);
		Assert.assertEquals(TestService.class, service.getClass());
	}
	
}

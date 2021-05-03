package org.jboss.tools.hibernate.spi;

import java.lang.reflect.Field;

import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.jboss.tools.hibernate.spi.internal.TestService;
import org.junit.Assert;
import org.junit.Test;

public class RuntimeServiceManagerTest {

	@Test
	public void testFindService() {
		IService service = RuntimeServiceManager.findService("0.0.0.Test");
		Assert.assertNotNull(service);
		Assert.assertEquals(TestService.class, service.getClass());
	}
	
	@Test
	public void testGetDefault() {
		IService service = RuntimeServiceManager.getInstance().getDefaultService();
		Assert.assertSame(RuntimeServiceManager.findService("0.0.0.Test"), service);
	}
	
	@Test
	public void testGetInstance() throws Exception {
		Field instanceField = RuntimeServiceManager.class.getDeclaredField("INSTANCE");
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		Assert.assertNotNull(instance);
		Assert.assertSame(instance, RuntimeServiceManager.getInstance());
	}
	

}

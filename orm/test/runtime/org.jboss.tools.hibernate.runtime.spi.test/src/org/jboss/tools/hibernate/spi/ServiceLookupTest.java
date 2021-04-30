package org.jboss.tools.hibernate.spi;

import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.jboss.tools.hibernate.spi.internal.TestService;
import org.junit.Assert;
import org.junit.Test;

public class ServiceLookupTest {

	@Test
	public void testFindService() {
		IService service = RuntimeServiceManager.findService("0.0.0.Test");
		Assert.assertNotNull(service);
		Assert.assertEquals(TestService.class, service.getClass());
	}
	
	@Test
	public void testGetDefault() {
		IService service = RuntimeServiceManager.getDefaultService();
		Assert.assertSame(RuntimeServiceManager.findService("0.0.0.Test"), service);
	}

}

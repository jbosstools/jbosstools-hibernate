package org.jboss.tools.hibernate.spi;

import org.jboss.tools.hibernate.spi.internal.TestService;
import org.junit.Assert;
import org.junit.Test;

public class ServiceLookupTest {

	@Test
	public void testFindService() {
		IService service = ServiceLookup.findService("test");
		Assert.assertNotNull(service);
		Assert.assertEquals(TestService.class, service.getClass());
	}

}

package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.cfg.AnnotationConfiguration;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplTest {
	
	private ServiceImpl service = new ServiceImpl();

	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration configuration = service.newAnnotationConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof AnnotationConfiguration);
	}
	
}

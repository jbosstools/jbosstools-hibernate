package org.hibernate.console.util;

import org.jboss.tools.hibernate.util.HibernateHelper;
import org.junit.Assert;
import org.junit.Test;

public class HibernateHelperTest {
	
	@Test
	public void testGetHibernateService() {
		Assert.assertNotNull(HibernateHelper.INSTANCE.getHibernateService());
	}

}

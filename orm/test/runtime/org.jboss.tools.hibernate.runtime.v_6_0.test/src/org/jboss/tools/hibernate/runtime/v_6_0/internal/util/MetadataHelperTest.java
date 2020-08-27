package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

public class MetadataHelperTest {
	
	@Test
	public void testGetMetadataSources() {
		MetadataSources mds1 = new MetadataSources();
		Configuration configuration = new Configuration();
		MetadataSources mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertNotSame(mds1, mds2);
		configuration = new Configuration(mds1);
		mds2 = MetadataHelper.getMetadataSources(configuration);
		assertNotNull(mds2);
		assertSame(mds1, mds2);
	}

}

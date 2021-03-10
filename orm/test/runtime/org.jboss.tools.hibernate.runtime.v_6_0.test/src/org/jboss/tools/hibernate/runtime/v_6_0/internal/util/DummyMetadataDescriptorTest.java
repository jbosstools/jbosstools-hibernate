package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.junit.Test;

public class DummyMetadataDescriptorTest {
	
	@Test
	public void testConstruction() {
		assertTrue(new DummyMetadataDescriptor() instanceof MetadataDescriptor);
	}
	
	@Test
	public void testGetProperties() {
		assertNull(new DummyMetadataDescriptor().getProperties());
	}

}

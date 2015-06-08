package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.Collections;
import java.util.Set;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArtifactCollectorFacadeTest {
	
	private static Set<String> FILE_TYPES = Collections.emptySet();
	
	private String methodName = null;
	private Object[] arguments = null;
	
	private IArtifactCollector artifactCollector = null; 
	
	@Before
	public void setUp() {
		methodName = null;
		arguments = null;
		artifactCollector = new AbstractArtifactCollectorFacade(null, new TestArtifactCollector()) {};
	}
	
	@Test
	public void testGetFileTypes() {
		Assert.assertSame(FILE_TYPES, artifactCollector.getFileTypes());
		Assert.assertEquals("getFileTypes", methodName);
		Assert.assertArrayEquals(new Object[] {}, arguments);
	}

	private class TestArtifactCollector extends ArtifactCollector {
		public Set<String> getFileTypes() {
			methodName = "getFileTypes";
			arguments = new Object[] {};
			return FILE_TYPES;
		}
	}
	
}

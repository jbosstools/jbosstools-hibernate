package org.jboss.tools.hibernate.runtime.v_6_2.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArtifactCollectorFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final Set<String> FILE_TYPES = new HashSet<String>();
	private static final File[] FILES = new File[] { new File("foobar") };
	
	private IArtifactCollector artifactCollectorFacade = null;
	private ArtifactCollector artifactCollectorTarget = null;	

	@BeforeEach
	public void beforeEach() {
		artifactCollectorTarget = new TestArtifactCollector();
		artifactCollectorFacade = new AbstractArtifactCollectorFacade(FACADE_FACTORY, artifactCollectorTarget) {};
	}
	
	@Test
	public void testGetFileTypes() {
		assertSame(FILE_TYPES, artifactCollectorFacade.getFileTypes());
	}
	
	@Test
	public void testFormatFiles() {
		assertFalse(((TestArtifactCollector)artifactCollectorTarget).formatted);
		artifactCollectorFacade.formatFiles();
		assertTrue(((TestArtifactCollector)artifactCollectorTarget).formatted);
	}
	
	@Test
	public void testGetFiles() {
		assertSame(FILES, artifactCollectorFacade.getFiles("foobar"));
	}
	
	private class TestArtifactCollector extends DefaultArtifactCollector {
		
		private boolean formatted = false;
		
		@Override
		public Set<String> getFileTypes() {
			return FILE_TYPES;
		}
		
		@Override
		public void formatFiles() {
			formatted = true;
		}
		
		@Override
		public File[] getFiles(String str) {
			return FILES;
		}
		
	}

}

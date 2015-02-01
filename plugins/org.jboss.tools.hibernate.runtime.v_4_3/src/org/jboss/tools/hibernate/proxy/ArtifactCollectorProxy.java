package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Set;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;

public class ArtifactCollectorProxy implements IArtifactCollector {
	
	private ArtifactCollector target = new ArtifactCollector();
	
	public Object getTarget() {
		return target;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getFileTypes() {
		return target.getFileTypes();
	}

	@Override
	public void formatFiles() {
		target.formatFiles();
	}

	@Override
	public File[] getFiles(String type) {
		return target.getFiles(type);
	}

}

package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.common.internal.Util;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;

public abstract class AbstractArtifactCollector implements IArtifactCollector {
	
	protected Object target = null;
	
	protected String getTargetClassName() {
		return "org.hibernate.tool.hbm2x.ArtifactCollector";
	}
	
	public Object getTarget() {
		if (target == null) {
			target = Util.getInstance(getTargetClassName(), this);
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getFileTypes() {
		return (Set<String>)Util.invokeMethod(
				getTarget(), 
				"getFileTypes", 
				new Class[] {},
				new Object[] {});
	}

	@Override
	public void formatFiles() {
		Util.invokeMethod(
				getTarget(), 
				"formatFiles", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public File[] getFiles(String string) {
		return (File[])Util.invokeMethod(
				getTarget(), 
				"getFiles", 
				new Class[] {String.class}, 
				new Object[] { string });
	}

}

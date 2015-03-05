package org.jboss.tools.hibernate.runtime.spi;

public interface IFacadeFactory {
	
	ClassLoader getClassLoader();
	IArtifactCollector createArtifactCollector();
	ICfg2HbmTool createCfg2HbmTool();

}

package org.jboss.tools.hibernate.runtime.spi;

public interface IFacadeFactory {
	
	IArtifactCollector createArtifactCollector();
	ICfg2HbmTool createCfg2HbmTool();

}

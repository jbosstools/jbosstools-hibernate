package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public IArtifactCollector createArtifactCollector() {
		return new ArtifactCollectorFacadeImpl(this);
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return new Cfg2HbmToolFacadeImpl();
	}
	
}

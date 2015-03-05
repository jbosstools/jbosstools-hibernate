package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public IArtifactCollector createArtifactCollector() {
		return new AbstractArtifactCollectorFacade(this) {};
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return new Cfg2HbmToolFacadeImpl(this);
	}
	
}

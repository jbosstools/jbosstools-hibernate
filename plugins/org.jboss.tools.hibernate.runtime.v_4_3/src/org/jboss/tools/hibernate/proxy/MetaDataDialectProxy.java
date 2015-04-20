package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.jboss.tools.hibernate.runtime.common.AbstractMetaDataDialectFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class MetaDataDialectProxy extends AbstractMetaDataDialectFacade {

	public MetaDataDialectProxy(IFacadeFactory facadeFactory, MetaDataDialect mdd) {
		super(facadeFactory, mdd);
	}

	public MetaDataDialect getTarget() {
		return (MetaDataDialect)super.getTarget() ;
	}

}

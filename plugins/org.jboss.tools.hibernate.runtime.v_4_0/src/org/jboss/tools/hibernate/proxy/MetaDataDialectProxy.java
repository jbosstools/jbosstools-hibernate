package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.jboss.tools.hibernate.runtime.common.AbstractMetaDataDialectFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class MetaDataDialectProxy extends AbstractMetaDataDialectFacade {

	private MetaDataDialect target = null;

	public MetaDataDialectProxy(IFacadeFactory facadeFactory, MetaDataDialect mdd) {
		super(facadeFactory, mdd);
		target = mdd;
	}

	public MetaDataDialect getTarget() {
		return target ;
	}

}

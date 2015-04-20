package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;

public class MetaDataDialectProxy implements IMetaDataDialect {

	private MetaDataDialect target = null;

	public MetaDataDialectProxy(IFacadeFactory facadeFactory, MetaDataDialect mdd) {
		target = mdd;
	}

	public MetaDataDialect getTarget() {
		return target ;
	}

}

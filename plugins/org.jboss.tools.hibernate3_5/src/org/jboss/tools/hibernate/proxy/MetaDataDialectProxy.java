package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;

public class MetaDataDialectProxy implements IMetaDataDialect {

	private MetaDataDialect target = null;

	public MetaDataDialectProxy(MetaDataDialect mdd) {
		target = mdd;
	}

	MetaDataDialect getTarget() {
		return target ;
	}

}

package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.AbstractColumnFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDialect;

public class ColumnFacadeImpl extends AbstractColumnFacade {

	public ColumnFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getSqlType(IDialect dialect, IConfiguration configuration) {
		Column targetColumn = (Column)getTarget();
		Configuration configurationTarget = 
				(Configuration)((IFacade)configuration).getTarget();
		Dialect dialectTarget = (Dialect)((IFacade)dialect).getTarget();
		return targetColumn.getSqlType(
				dialectTarget, 
				MetadataHelper.getMetadata(configurationTarget));
	}
}

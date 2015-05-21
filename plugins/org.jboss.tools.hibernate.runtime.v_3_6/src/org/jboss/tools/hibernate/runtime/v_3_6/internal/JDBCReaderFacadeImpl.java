package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.cfg.reveng.JDBCReader;
import org.jboss.tools.hibernate.runtime.common.AbstractJDBCReaderFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class JDBCReaderFacadeImpl extends AbstractJDBCReaderFacade {
	
	public JDBCReaderFacadeImpl(IFacadeFactory facadeFactory, JDBCReader reader) {
		super(facadeFactory, reader);
	}

	public JDBCReader getTarget() {
		return (JDBCReader)super.getTarget();
	}

}

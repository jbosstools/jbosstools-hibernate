package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import org.hibernate.dialect.Dialect;

public class MockDialect extends Dialect {

	@Override
	public int getVersion() {
		return 0;
	}

}

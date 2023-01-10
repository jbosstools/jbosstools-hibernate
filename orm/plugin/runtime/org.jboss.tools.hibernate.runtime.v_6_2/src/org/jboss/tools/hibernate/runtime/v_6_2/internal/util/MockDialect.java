package org.jboss.tools.hibernate.runtime.v_6_2.internal.util;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SimpleDatabaseVersion;

public class MockDialect extends Dialect {
	
	public MockDialect() {
		super(new SimpleDatabaseVersion(Integer.MAX_VALUE, Integer.MIN_VALUE));
	
	}
	
}

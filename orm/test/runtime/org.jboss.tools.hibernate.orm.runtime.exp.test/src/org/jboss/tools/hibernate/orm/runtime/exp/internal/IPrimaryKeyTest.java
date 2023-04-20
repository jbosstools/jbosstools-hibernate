package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPrimaryKeyTest {

	private IPrimaryKey primaryKeyFacade = null; 
	private PrimaryKey primaryKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		primaryKeyTarget = new PrimaryKey(new Table(""));
		primaryKeyFacade = (IPrimaryKey)GenericFacadeFactory.createFacade(
				IPrimaryKey.class, 
				primaryKeyTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(primaryKeyFacade);
		assertNotNull(primaryKeyTarget);
	}

}

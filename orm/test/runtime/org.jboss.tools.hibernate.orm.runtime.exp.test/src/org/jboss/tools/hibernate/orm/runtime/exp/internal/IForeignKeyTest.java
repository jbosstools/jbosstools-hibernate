package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IForeignKeyTest {

	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		foreignKeyTarget = new ForeignKey();
		foreignKeyFacade = (IForeignKey)GenericFacadeFactory.createFacade(IForeignKey.class, foreignKeyTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(foreignKeyFacade);
		assertNotNull(foreignKeyTarget);
	}

}

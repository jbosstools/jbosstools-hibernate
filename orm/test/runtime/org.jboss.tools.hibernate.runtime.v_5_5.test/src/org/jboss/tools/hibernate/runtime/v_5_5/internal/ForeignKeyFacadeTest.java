package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForeignKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKey = null;
	
	@BeforeEach
	public void beforeEach() {
		foreignKey = new ForeignKey();
		foreignKeyFacade = new AbstractForeignKeyFacade(FACADE_FACTORY, foreignKey) {};
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(foreignKeyFacade);
	}
	
}

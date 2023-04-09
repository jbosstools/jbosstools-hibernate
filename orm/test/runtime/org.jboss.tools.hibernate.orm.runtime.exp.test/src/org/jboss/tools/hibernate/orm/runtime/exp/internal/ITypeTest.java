package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.orm.jbt.type.ClassType;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory;
import org.hibernate.type.ArrayType;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.Test;

public class ITypeTest {
	
	@Test
	public void testIsCollectionType() {
		// first try type that is not a collection type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertFalse(classTypeFacade.isCollectionType());
		// next try a collection type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class)));
		assertTrue(arrayTypeFacade.isCollectionType());
	}
	
}

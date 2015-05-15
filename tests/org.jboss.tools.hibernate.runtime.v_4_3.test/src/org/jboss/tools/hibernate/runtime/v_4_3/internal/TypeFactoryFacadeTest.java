package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.type.StandardBasicTypes;
import org.jboss.tools.hibernate.proxy.TypeFactoryProxy;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TypeFactoryFacadeTest {
	
	private TypeFactoryProxy typeFactory;
	
	@Before
	public void setUp() {
		typeFactory = new TypeFactoryProxy(new FacadeFactoryImpl(), null);
	}

	@Test
	public void testGetBooleanType() {
		IType type = typeFactory.getBooleanType();
		Assert.assertNotNull(type);
		Assert.assertEquals(StandardBasicTypes.BOOLEAN, getTarget(type));
	}
	
	private Object getTarget(Object object) {
		return Util.invokeMethod(
				object, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
	}

}

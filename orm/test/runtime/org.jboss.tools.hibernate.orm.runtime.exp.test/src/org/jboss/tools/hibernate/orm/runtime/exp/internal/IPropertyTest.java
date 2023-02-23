package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPropertyTest {
	
	private IProperty propertyFacade = null;
	private Property propertyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		propertyFacade = NewFacadeFactory.INSTANCE.createProperty();
		propertyTarget = (Property)((IFacade)propertyFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(propertyFacade);
		assertNotNull(propertyTarget);
	}

	@Test
	public void testGetValue() {
		assertNull(propertyFacade.getValue());
		Value valueTarget = createValue();
		propertyTarget.setValue(valueTarget);
		IValue valueFacade = propertyFacade.getValue();
		assertNotNull(valueFacade);
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testSetName() {
		assertNotEquals("foo", propertyTarget.getName());
		propertyFacade.setName("foo");
		assertEquals("foo", propertyTarget.getName());
	}
	
	@Test
	public void testSetPersistentClass() {
		assertNull(propertyTarget.getPersistentClass());
		IPersistentClass persistentClassFacade = NewFacadeFactory.INSTANCE.createRootClass();
		PersistentClassWrapper persistentClassTarget = 
				(PersistentClassWrapper)((IFacade)persistentClassFacade).getTarget();
		propertyFacade.setPersistentClass(persistentClassFacade);
		assertSame(persistentClassTarget.getWrappedObject(), propertyTarget.getPersistentClass());
	}
	
	private Value createValue() {
		return (Value)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Value.class }, 
				new InvocationHandler() {	
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
}

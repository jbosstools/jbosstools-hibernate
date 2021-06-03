package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Value;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final Type TYPE = new StringType();
	
	private Property propertyTarget = null;
	private IProperty propertyFacade = null;
	private Boolean valueBoolean = false;

	@BeforeEach
	public void beforeEach() {
		propertyTarget = new Property();
		propertyFacade = new AbstractPropertyFacade(FACADE_FACTORY, propertyTarget) {};
	}
	
	@Test
	public void testGetValue() throws Exception {
		Field field = AbstractPropertyFacade.class.getDeclaredField("value");
		field.setAccessible(true);
		assertNull(field.get(propertyFacade));
		assertNull(propertyFacade.getValue());
		Value valueTarget = createValue();
		propertyTarget.setValue(valueTarget);
		IValue valueFacade = propertyFacade.getValue();
		assertNotNull(valueFacade);
		assertSame(valueFacade, field.get(propertyFacade));
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testSetName() {
		assertNotEquals("foo", propertyTarget.getName());
		propertyFacade.setName("foo");
		assertEquals("foo", propertyTarget.getName());
	}
	
	@Test
	public void testSetPersistentClass() throws Exception {
		Field field = AbstractPropertyFacade.class.getDeclaredField("persistentClass");
		field.setAccessible(true);
		assertNull(field.get(propertyFacade));
		assertNull(propertyTarget.getPersistentClass());
		PersistentClass persistentClassTarget = new RootClass(null);
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClassTarget);
		propertyFacade.setPersistentClass(persistentClassFacade);
		assertSame(persistentClassFacade, field.get(propertyFacade));
		assertSame(persistentClassTarget, propertyTarget.getPersistentClass());
	}
	
	private Value createValue() {
		return (Value)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Value.class }, 
				new InvocationHandler() {	
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("getType".equals(method.getName())) {
							return TYPE;
						}
						if ("hasAnyInsertableColumns".equals(method.getName())) {
							return valueBoolean;
						}
						if ("hasAnyUpdatableColumns".equals(method.getName())) {
							return valueBoolean;
						}
						if ("isNullable".equals(method.getName())) {
							return false;
						}
						return null;
					}
		});
	}
	
}

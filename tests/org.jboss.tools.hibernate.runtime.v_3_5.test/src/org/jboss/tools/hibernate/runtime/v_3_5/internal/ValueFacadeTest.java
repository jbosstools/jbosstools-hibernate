package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.Assert;
import org.junit.Test;

public class ValueFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isSimpleValue());
	}
	
	@Test
	public void testIsCollection() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isCollection());
		valueTarget = new Set(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isCollection());
	}
	
	@Test
	public void testGetCollectionElement() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IValue collectionElement = valueFacade.getCollectionElement();
		Assert.assertNull(collectionElement);
		Set set = new Set(null);
		set.setElement(valueTarget);
		valueFacade = FACADE_FACTORY.createValue(set);
		collectionElement = valueFacade.getCollectionElement();
		Assert.assertNotNull(collectionElement);
		Assert.assertSame(valueTarget, ((IFacade)collectionElement).getTarget());
	}
	
	@Test 
	public void testIsOneToMany() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(null);
		valueFacade = FACADE_FACTORY.createValue(oneToMany);
		Assert.assertTrue(valueFacade.isOneToMany());
	}

	@Test 
	public void testIsManyToOne() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isManyToOne());
		ManyToOne manyToOne = new ManyToOne(null);
		valueFacade = FACADE_FACTORY.createValue(manyToOne);
		Assert.assertTrue(valueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToOne());
		OneToOne oneToOne = new OneToOne(null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(oneToOne);
		Assert.assertTrue(valueFacade.isOneToOne());
	}
	
	@Test
	public void testIsMap() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isMap());
		Map map = new Map(null);
		valueFacade = FACADE_FACTORY.createValue(map);
		Assert.assertTrue(valueFacade.isMap());
	}

	@Test
	public void testIsComponent() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isComponent());
		Component component = new Component(new RootClass());
		valueFacade = FACADE_FACTORY.createValue(component);
		Assert.assertTrue(valueFacade.isComponent());
	}

	@Test
	public void testIsEmbedded() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.isEmbedded());
		Component component = new Component(new RootClass());
		valueFacade = FACADE_FACTORY.createValue(component);
		component.setEmbedded(true);
		Assert.assertTrue(valueFacade.isEmbedded());
		component.setEmbedded(false);
		Assert.assertFalse(valueFacade.isEmbedded());
	}

}

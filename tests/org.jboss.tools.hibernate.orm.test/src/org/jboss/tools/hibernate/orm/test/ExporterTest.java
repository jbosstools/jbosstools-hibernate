package org.jboss.tools.hibernate.orm.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.eclipse.launch.ExporterFactoryPropertySource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExporterTest {

	private HashMap<String, ExporterProperty> map;
	private ExporterFactory factory;
	private ExporterDefinition definition;
	
	@Before
	public void setUp() throws Exception {
		map = new HashMap<String, ExporterProperty>();
		map.put("ejb3", new ExporterProperty("ejb3", "Use ejb3 syntax", "true", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		definition = new ExporterDefinition("exporterClass",  //$NON-NLS-1$
								"exporterDescription", //$NON-NLS-1$
								"exporterId", //$NON-NLS-1$
								map,
								null);
		
		
		factory = new ExporterFactory(definition, definition.getId());

	}

	@After
	public void tearDown() throws Exception {
		map = null;
		factory = null;
		definition = null;
	}

	@Test
	public void testExporters() {
		
		Map<String, ExporterProperty> properties = definition.getExporterProperties();
		
		Assert.assertEquals(properties, map);
		
		
		Map<String, ExporterProperty> defaultProperties = factory.getDefaultExporterProperties();
		Assert.assertEquals(defaultProperties, map);
		
		
		// pure local manipulation 
		Assert.assertNull(factory.setProperty("localValue", "true")); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertEquals("true", factory.getPropertyValue("localValue"));  //$NON-NLS-1$//$NON-NLS-2$
		Assert.assertTrue(factory.hasLocalValueFor("localValue")); //$NON-NLS-1$
		factory.removeProperty( "localValue" ); //$NON-NLS-1$
		Assert.assertNull(factory.getPropertyValue( "localValue" )); //$NON-NLS-1$
		Assert.assertFalse(factory.hasLocalValueFor("localValue")); //$NON-NLS-1$
		
	}
	
	@Test
	public void testDefaultValues() {
		
		Assert.assertEquals("true", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
		
		Assert.assertFalse(factory.hasLocalValueFor("ejb3")); //$NON-NLS-1$
		
		factory.setProperty( "ejb3", "false");  //$NON-NLS-1$//$NON-NLS-2$
		Assert.assertEquals("false", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
		
		factory.removeProperty( "ejb3" ); //$NON-NLS-1$
		
		Assert.assertEquals("true", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	@Test
	public void testExporterEnablement() {
		
		Assert.assertTrue(factory.isEnabled());
		
		factory.setEnabled(false);
		
		Assert.assertFalse(factory.isEnabled());
	}
	
	@Test
	public void testPropertySource() {
		
		ExporterFactoryPropertySource ips = new ExporterFactoryPropertySource(factory); 
		
		IPropertyDescriptor[] propertyDescriptors = ips.getPropertyDescriptors();
		
		Assert.assertNotNull(propertyDescriptors);
		
		Assert.assertEquals(0, propertyDescriptors.length);
		
		factory.setProperty( "something", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		
		propertyDescriptors = ips.getPropertyDescriptors();
		
		Assert.assertEquals(1, propertyDescriptors.length);		
		
	}
}

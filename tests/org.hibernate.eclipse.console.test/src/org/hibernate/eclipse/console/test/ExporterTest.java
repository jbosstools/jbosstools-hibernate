package org.hibernate.eclipse.console.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.eclipse.launch.ExporterFactoryPropertySource;

public class ExporterTest extends TestCase {

	private HashMap<String, ExporterProperty> map;
	private ExporterFactory factory;
	private ExporterDefinition definition;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		map = new HashMap<String, ExporterProperty>();
		map.put("ejb3", new ExporterProperty("ejb3", "Use ejb3 syntax", "true", true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		definition = new ExporterDefinition("exporterClass",  //$NON-NLS-1$
								"exporterDescription", //$NON-NLS-1$
								"exporterId", //$NON-NLS-1$
								map,
								null);
		
		
		factory = new ExporterFactory(definition, definition.getId());

	}

	protected void tearDown() throws Exception {
		map = null;
		factory = null;
		definition = null;
	}

	public void testExporters() {
		
		Map<String, ExporterProperty> properties = definition.getExporterProperties();
		
		assertEquals(properties, map);
		
		
		Map<String, ExporterProperty> defaultProperties = factory.getDefaultExporterProperties();
		assertEquals(defaultProperties, map);
		
		
		// pure local manipulation 
		assertNull(factory.setProperty("localValue", "true")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("true", factory.getPropertyValue("localValue"));  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue(factory.hasLocalValueFor("localValue")); //$NON-NLS-1$
		factory.removeProperty( "localValue" ); //$NON-NLS-1$
		assertNull(factory.getPropertyValue( "localValue" )); //$NON-NLS-1$
		assertFalse(factory.hasLocalValueFor("localValue")); //$NON-NLS-1$
		
	}
	
	public void testDefaultValues() {
		
		assertEquals("true", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
		
		assertFalse(factory.hasLocalValueFor("ejb3")); //$NON-NLS-1$
		
		factory.setProperty( "ejb3", "false");  //$NON-NLS-1$//$NON-NLS-2$
		assertEquals("false", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
		
		factory.removeProperty( "ejb3" ); //$NON-NLS-1$
		
		assertEquals("true", factory.getPropertyValue( "ejb3" ));  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	public void testExporterEnablement() {
		
		assertTrue(factory.isEnabled());
		
		factory.setEnabled(false);
		
		assertFalse(factory.isEnabled());
	}
	
	public void testPropertySource() {
		
		ExporterFactoryPropertySource ips = new ExporterFactoryPropertySource(factory); 
		
		IPropertyDescriptor[] propertyDescriptors = ips.getPropertyDescriptors();
		
		assertNotNull(propertyDescriptors);
		
		assertEquals(0, propertyDescriptors.length);
		
		factory.setProperty( "something", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		
		propertyDescriptors = ips.getPropertyDescriptors();
		
		assertEquals(1, propertyDescriptors.length);		
		
	}
}

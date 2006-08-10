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

	private HashMap map;
	private ExporterFactory factory;
	private ExporterDefinition definition;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		map = new HashMap();
		map.put("ejb3", new ExporterProperty("ejb3", "Use ejb3 syntax", "true", true));
		definition = new ExporterDefinition("exporterClass", 
								"exporterDescription",
								"exporterId",
								map,
								null);
		
		
		factory = new ExporterFactory(definition);

	}
	public void testExporters() {
		
		Map properties = definition.getProperties();
		
		assertEquals(properties, map);
		
		
		Map defaultProperties = factory.getDefaultExporterProperties();
		assertEquals(defaultProperties, map);
		
		
		// pure local manipulation 
		assertNull(factory.setProperty("localValue", "true"));
		assertEquals("true", factory.getPropertyValue("localValue"));
		assertTrue(factory.hasLocalValueFor("localValue"));
		factory.removeProperty( "localValue" );
		assertNull(factory.getPropertyValue( "localValue" ));
		assertFalse(factory.hasLocalValueFor("localValue"));
		
	}
	
	public void testDefaultValues() {
		
		assertEquals("true", factory.getPropertyValue( "ejb3" ));
		
		assertFalse(factory.hasLocalValueFor("ejb3"));
		
		factory.setProperty( "ejb3", "false");
		assertEquals("false", factory.getPropertyValue( "ejb3" ));
		
		factory.removeProperty( "ejb3" );
		
		assertEquals("true", factory.getPropertyValue( "ejb3" ));
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
		
		factory.setProperty( "something", "true" );
		
		propertyDescriptors = ips.getPropertyDescriptors();
		
		assertEquals(1, propertyDescriptors.length);		
		
	}
}

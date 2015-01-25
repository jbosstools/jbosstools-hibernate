package org.jboss.tools.hibernate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.xml.sax.EntityResolver;

public class OpenMappingUtilsEjb3 {
	
	private OpenMappingUtilsEjb3() {}

	public static List<String> enumDocuments(String persistenceUnitName, EntityResolver entityResolver) {
		List<String> result = new ArrayList<String>();
		List<ParsedPersistenceXmlDescriptor> persistenceUnits = 
				PersistenceXmlParser.locatePersistenceUnits(new Properties());
		for (ParsedPersistenceXmlDescriptor descriptor : persistenceUnits) {
			if (descriptor.getName().equals(persistenceUnitName)) {
				result.addAll(descriptor.getMappingFileNames());
			}
		}
		return result;
	}
	
	
}

package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

public class JpaMappingFileHelper {
	
	private JpaMappingFileHelper() {}

	public static List<String> findMappingFiles(String persistenceUnitName) {
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

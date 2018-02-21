package org.jboss.tools.hibernate.orm.test.utils;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class CfgXmlFileHelper {

	private static final String CFG_XML_HEADER = 
			"<!DOCTYPE hibernate-configuration PUBLIC                                                                     \n" +
			"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                                                        \n" +
			"	'http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd'>                                       \n" +
			"                                                                                                             \n" +
			"<hibernate-configuration>                                                                                    \n" +
			"	<session-factory>                                                                                         \n" +
			"       <property name='dialect'>org.hibernate.dialect.HSQLDialect</property>                                 \n" ;
	
	private static final String CFG_XML_TRAILER = 
			"	</session-factory>                                                                                        \n" +
			"</hibernate-configuration>                                                                                    " ;
	
	private static final String RESOURCE_MAPPING_HEADER = "       <mapping resource='";
	
	private static final String RESOURCE_MAPPING_TRAILER = "' /> \n";
	
	public static String constructCfgXmlString(String packageName, IProject project) {
		StringBuffer buffer = new StringBuffer(CFG_XML_HEADER);
		String resourcePath = "/" + packageName.replace('.', '/');
		IFolder folder = project.getFolder("/src" + resourcePath);
		try {
			for (IResource resource : folder.members()) {
				if (resource.getName().endsWith("hbm.xml")) {
					buffer.append(
							RESOURCE_MAPPING_HEADER + 
							resourcePath + 
							"/" + 
							resource.getName() +
							RESOURCE_MAPPING_TRAILER); 
				}
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		buffer.append(CFG_XML_TRAILER);
		return buffer.toString();
	}
	
}

/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateExtensionManager {
	
	public static final String HIBERNATEEXTENSION_EXTENSION_ID = "org.hibernate.eclipse.hibernateextension"; //$NON-NLS-1$
	
	private static Map<String, HibernateExtensionDefinition> hibernateExtensions;

	private static IExtension[] findExtensions(String extensionId) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionId);
		return extensionPoint.getExtensions();
	}

	public static HibernateExtensionDefinition[] findHiberanteExtensionDefinitions() {
		List<HibernateExtensionDefinition> hibernateExtensions = new ArrayList<HibernateExtensionDefinition>();
		IExtension[] extensions = findExtensions(HIBERNATEEXTENSION_EXTENSION_ID);
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement elements[] = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				HibernateExtensionDefinition extensiodDefinition = new HibernateExtensionDefinition(
						elements[j]);
				hibernateExtensions.add(extensiodDefinition);
			}
		}

		return hibernateExtensions.toArray(new HibernateExtensionDefinition[hibernateExtensions.size()]);
	}

	/**
	 * return map of ExporterDefinitions keyed by id
	 */
	public static synchronized Map<String, HibernateExtensionDefinition> getHibernateExtensionDefinitionsAsMap() {
		if (hibernateExtensions == null){
			//TODO add good comparator here(we often need the keys ordered)
			hibernateExtensions = new TreeMap<String, HibernateExtensionDefinition>();

			HibernateExtensionDefinition[] findExporterDefinitions = findHiberanteExtensionDefinitions();
			for (int i = 0; i < findExporterDefinitions.length; i++) {
				HibernateExtensionDefinition exporterDefinition = findExporterDefinitions[i];
				hibernateExtensions.put(exporterDefinition.getHibernateVersion(),
						exporterDefinition);
			}
		}

		return hibernateExtensions;
	}
	
	public static HibernateExtensionDefinition findHibernateExtensionDefinition(String hibernateVersion){
		return getHibernateExtensionDefinitionsAsMap().get(hibernateVersion);
	}

}

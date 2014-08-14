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
package org.hibernate.eclipse.console.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.common.HibernateExtension;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleExtensionManager {
	
	public static final String CONSOLEEXTENSION_EXTENSION_ID = "org.hibernate.eclipse.console.consoleextension"; //$NON-NLS-1$
	
	private static Map<String, ConsoleExtensionDefinition> consoleExtensionDefinitions;
	
	private static Map<HibernateExtension, ConsoleExtension> consoleExtensions = new HashMap<HibernateExtension, ConsoleExtension>();

	private static IExtension[] findExtensions(String extensionId) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionId);
		return extensionPoint.getExtensions();
	}

	private static ConsoleExtensionDefinition[] findConsoleExtensionDefinitions() {
		List<ConsoleExtensionDefinition> exporters = new ArrayList<ConsoleExtensionDefinition>();

		IExtension[] extensions = findExtensions(CONSOLEEXTENSION_EXTENSION_ID);
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement elements[] = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				ConsoleExtensionDefinition extensiodDefinition = new ConsoleExtensionDefinition(
						elements[j]);
				exporters.add(extensiodDefinition);
			}
		}

		return exporters.toArray(new ConsoleExtensionDefinition[exporters.size()]);
	}

	/**
	 * return map of ExporterDefinitions keyed by id
	 */
	private static synchronized Map<String, ConsoleExtensionDefinition> getConsoleExtensionDefinitionsAsMap() {
		if (consoleExtensionDefinitions == null){
			//TODO add good comparator here(we often need the keys ordered)
			consoleExtensionDefinitions = new TreeMap<String, ConsoleExtensionDefinition>();

			ConsoleExtensionDefinition[] findExporterDefinitions = findConsoleExtensionDefinitions();
			for (int i = 0; i < findExporterDefinitions.length; i++) {
				ConsoleExtensionDefinition exporterDefinition = findExporterDefinitions[i];
				consoleExtensionDefinitions.put(exporterDefinition.getHibernateVersion(),
						exporterDefinition);
			}
		}

		return consoleExtensionDefinitions;
	}
	
	private static ConsoleExtensionDefinition findConsoleExtensionDefinition(String hibernateVersion){
		return getConsoleExtensionDefinitionsAsMap().get(hibernateVersion);
	}
	
	
	public static ConsoleExtension getConsoleExtension(HibernateExtension hibernateExtension){
		if (hibernateExtension != null){
			if (!consoleExtensions.containsKey(hibernateExtension)){
				ConsoleExtensionDefinition definition = findConsoleExtensionDefinition(hibernateExtension.getHibernateVersion());
				if (definition != null){
					consoleExtensions.put(hibernateExtension, definition.createConsoleExtensionInstance(hibernateExtension));
				}
			}
			return consoleExtensions.get(hibernateExtension);
		}
		return null;
	}
	
	public static ConsoleExtension getConsoleExtension(ConsoleConfiguration consoleConfiguration){
		if (consoleConfiguration != null){
			return getConsoleExtension(consoleConfiguration.getHibernateExtension());
		}
		return null;
	}

}

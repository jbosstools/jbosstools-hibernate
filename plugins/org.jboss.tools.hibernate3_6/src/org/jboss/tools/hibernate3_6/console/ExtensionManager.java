/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate3_6.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author dgeraskov
 *
 */
public class ExtensionManager
{
   public static final String EXPORTERS_EXTENSION_ID = "org.hibernate.eclipse.console.exporters"; //$NON-NLS-1$
   
   private static IExtension[] findExtensions (String extensionId)
   {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionId);
      return extensionPoint.getExtensions();
   }
   
   public static ExporterDefinition[] findExporterDefinitions ()
   {
      List<ExporterDefinition> exporters = new ArrayList<ExporterDefinition>();
      
      IExtension[] extensions = findExtensions(EXPORTERS_EXTENSION_ID);
      for (int i = 0; i < extensions.length; i++)
      {
         IConfigurationElement elements[] = extensions[i].getConfigurationElements();
         for (int j = 0; j < elements.length; j++)
         {
            ExporterDefinition exporter = new ExporterDefinition(elements[j]);
            exporters.add(exporter);
         }
      }
      
      return exporters.toArray(new ExporterDefinition[exporters.size()]);
   }
      
   /**
    * return map of ExporterDefinitions keyed by id
    */ 
   public static Map<String, ExporterDefinition> findExporterDefinitionsAsMap() {
	   Map<String, ExporterDefinition> result = new HashMap<String, ExporterDefinition>();

	   ExporterDefinition[] findExporterDefinitions = findExporterDefinitions();
	   for (int i = 0; i < findExporterDefinitions.length; i++) {
		   ExporterDefinition exporterDefinition = findExporterDefinitions[i];
		   result.put(exporterDefinition.getId(), exporterDefinition);
	   }

	   return result;
   }
}

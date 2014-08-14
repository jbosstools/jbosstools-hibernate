/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http:/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;

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

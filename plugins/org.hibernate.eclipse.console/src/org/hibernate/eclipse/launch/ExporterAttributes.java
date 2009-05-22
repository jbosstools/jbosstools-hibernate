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
package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;

// This class was created to centralize launch configuration attribute loading/saving
// (and also to clean up CodeGenerationLaunchDelegate considerably)
public class ExporterAttributes
{
   private boolean reverseEngineer, useOwnTemplates, enableJDK5, enableEJB3, preferBasicCompositeIds;
   private String consoleConfigurationName;
   private String revengSettings;
   private String revengStrategy;
   private String packageName;
   private String outputPath;
   private String templatePath;
   private List<ExporterFactory> exporterFactories;

   private boolean autoManyToManyDetection;
   private boolean autoOneToOneDetection;
   private boolean autoVersioning;

   public ExporterAttributes () { }

   public ExporterAttributes (ILaunchConfiguration configuration)
      throws CoreException
   {
      initialize(configuration);
   }

   public void initialize (ILaunchConfiguration configuration)
      throws CoreException
   {
      try {
         consoleConfigurationName = configuration.getAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME,"");         //$NON-NLS-1$
         outputPath = configuration.getAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR,""); //$NON-NLS-1$
         reverseEngineer = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, false);
         revengSettings = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, ""); //$NON-NLS-1$
         revengStrategy = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_STRATEGY, ""); //$NON-NLS-1$
         useOwnTemplates = configuration.getAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES,false);
         enableJDK5 = configuration.getAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5,false);
         enableEJB3 = configuration.getAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS,false);
         packageName = configuration.getAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME,""); //$NON-NLS-1$
         templatePath = configuration.getAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR,""); //$NON-NLS-1$
         preferBasicCompositeIds = configuration.getAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
         autoManyToManyDetection = configuration.getAttribute( HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
         autoOneToOneDetection = configuration.getAttribute( HibernateLaunchConstants.ATTR_AUTOMATIC_ONE_TO_ONE, true);
         autoVersioning = configuration.getAttribute( HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);


         if (!useOwnTemplates) {
        	 templatePath = null;
         }

         exporterFactories = readExporterFactories(configuration);
       } catch (CoreException e) {
         throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666));
      }
   }

   static String getLaunchAttributePrefix(String exporterId) {
	   	   return HibernateLaunchConstants.ATTR_EXPORTERS + "." + exporterId; //$NON-NLS-1$
   }

   @SuppressWarnings("unchecked")
private List<ExporterFactory> readExporterFactories(ILaunchConfiguration configuration) throws CoreException {

	   List<String> exporterNames = configuration.getAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, (List<String>)null);

	   if(exporterNames!=null) {
		   Map<String, ExporterDefinition> exDefinitions = ExtensionManager.findExporterDefinitionsAsMap();
		   List<ExporterFactory> factories = new ArrayList<ExporterFactory>();

		   for (String exporterId : exporterNames) {
			   String extensionId = configuration.getAttribute(getLaunchAttributePrefix(exporterId) + ".extension_id", (String)null); //$NON-NLS-1$

			   ExporterDefinition expDef = exDefinitions.get(extensionId);
			   if(expDef==null) {
				   String out = NLS.bind(HibernateConsoleMessages.ExporterAttributes_could_not_locate_exporter_for_in, extensionId, configuration.getName());
				   throw new HibernateConsoleRuntimeException(out);
			   } else {
				   ExporterFactory exporterFactory = new ExporterFactory( expDef, exporterId );
				   exporterFactory.isEnabled( configuration );
				   factories.add( exporterFactory );
				   Map<String, String> props = configuration.getAttribute( getLaunchAttributePrefix(exporterFactory.getId())
						   + ".properties", new HashMap<String, String>() ); //$NON-NLS-1$
				   exporterFactory.setProperties( props );
			   }
		   }
		   return factories;

	   } else {
		   // fall back to old way of reading if list of exporters does not exist.
		   ExporterDefinition[] exDefinitions = ExtensionManager.findExporterDefinitions();
		   List<ExporterFactory> factories = new ArrayList<ExporterFactory>();

		   for (int i = 0; i < exDefinitions.length; i++) {
			   ExporterDefinition expDef = exDefinitions[i];
			   ExporterFactory exporterFactory = new ExporterFactory( expDef, expDef.getId() );
			   exporterFactory.isEnabled( configuration );
			   factories.add( exporterFactory );
			   Map<String, String> props = configuration.getAttribute( getLaunchAttributePrefix(exporterFactory.getId())
					   + ".properties", new HashMap<String, String>() ); //$NON-NLS-1$
			   exporterFactory.setProperties( props );
		   }

		   return factories;
	   }
   }

   public static void saveExporterFactories(
			ILaunchConfigurationWorkingCopy configuration,
			List<ExporterFactory> exporterFactories, Set<ExporterFactory> enabledExporters, Set<String> deletedExporterIds) {


	   List<String> names = new ArrayList<String>();
	   for (ExporterFactory ef : exporterFactories) {
			configuration.setAttribute(getLaunchAttributePrefix(ef.getId()) + ".extension_id", ef.getExporterDefinition().getId()); //$NON-NLS-1$
			boolean enabled = enabledExporters.contains( ef );
			String propertiesId = getLaunchAttributePrefix(ef.getId()) + ".properties"; //$NON-NLS-1$
			names.add(ef.getId());
			ef.setEnabled( configuration, enabled, false );

			HashMap<String, String> map = new HashMap<String, String>(ef.getProperties());

			if(map.isEmpty()) {
				configuration.setAttribute( propertiesId, (Map<String, String>)null );
			} else {
				configuration.setAttribute( propertiesId, map );
			}
		}

		deletedExporterIds.removeAll(names);

		for (String deleted : deletedExporterIds) {
			configuration.setAttribute( getLaunchAttributePrefix( deleted ), (String)null);
			configuration.setAttribute(getLaunchAttributePrefix(deleted ) + ".extension_id", (String)null);						 //$NON-NLS-1$
			configuration.setAttribute(getLaunchAttributePrefix(deleted), (String)null);
		}

		configuration.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, names);
	}

	public static void oldSaveExporterFactories(
			ILaunchConfigurationWorkingCopy configuration,
			List<ExporterFactory> exporterFactories, List<ExporterFactory> enabledExporters) {

		for (ExporterFactory ef : exporterFactories) {
			boolean enabled = enabledExporters.contains( ef );
			String propertiesId = ef.getId() + ".properties"; //$NON-NLS-1$

			ef.setEnabled( configuration, enabled, true );

			HashMap<String, String> map = new HashMap<String, String>(ef.getProperties());

			if(map.isEmpty()) {
				configuration.setAttribute( propertiesId, (Map<String, String>)null );
			} else {
				configuration.setAttribute( propertiesId, map );
			}
		}
	}


    private Path pathOrNull(String p) {
        if(p==null || p.trim().length()==0) {
            return null;
        } else {
            return new Path(p);
        }
    }

   public String getOutputPath()
   {
      return outputPath;
   }

   public void setOutputPath(String outputPath)
   {
      this.outputPath = outputPath;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public String getRevengSettings()
   {
      return revengSettings;
   }

   public void setRevengSettings(String revengSettings)
   {
      this.revengSettings = revengSettings;
   }

   public String getRevengStrategy()
   {
      return revengStrategy;
   }

   public void setRevengStrategy(String revengStrategy)
   {
      this.revengStrategy = revengStrategy;
   }

   public String getTemplatePath()
   {
      return templatePath;
   }

   public void setTemplatePath(String templatePath)
   {
      this.templatePath = templatePath;
   }

   public String getConsoleConfigurationName()
   {
      return consoleConfigurationName;
   }

   public void setConsoleConfigurationName(String consoleConfigurationName)
   {
      this.consoleConfigurationName = consoleConfigurationName;
   }

   public boolean isEJB3Enabled()
   {
      return enableEJB3;
   }

   public void setEnableEJB3(boolean enableEJB3)
   {
      this.enableEJB3 = enableEJB3;
   }

   public boolean isJDK5Enabled()
   {
      return enableJDK5;
   }

   public void setEnableJDK5(boolean enableJDK5)
   {
      this.enableJDK5 = enableJDK5;
   }

   public boolean isPreferBasicCompositeIds()
   {
      return preferBasicCompositeIds;
   }

   public void setPreferBasicCompositeIds(boolean preferBasicCompositeIds)
   {
      this.preferBasicCompositeIds = preferBasicCompositeIds;
   }

   public boolean isReverseEngineer()
   {
      return reverseEngineer;
   }

   public void setReverseEngineer(boolean reverseEngineer)
   {
      this.reverseEngineer = reverseEngineer;
   }

   public boolean isUseOwnTemplates()
   {
      return useOwnTemplates;
   }

   public void setUseOwnTemplates(boolean useOwnTemplates)
   {
      this.useOwnTemplates = useOwnTemplates;
   }

   public List<ExporterFactory> getExporterFactories() {
	   return exporterFactories;
   }

   public boolean detectManyToMany() {
	   return autoManyToManyDetection;
   }

    public boolean detectOptimisticLock() {
    	return autoVersioning;
    }

	public boolean detectOneToOne() {
		return autoOneToOneDetection;
	}

}

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
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterInstance;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;

// This class was created to centralize launch configuration attribute loading/saving
// (and also to clean up CodeGenerationLaunchDelegate considerably)
public class LaunchAttributes
{
   private boolean reverseEngineer, useOwnTemplates, enableJDK5, enableEJB3, preferBasicCompositeIds;
   private String consoleConfigurationName;
   private String revengSettings;
   private String revengStrategy;
   private String packageName;
   private String outputPath;
   private String templatePath;
   private ArrayList exporterInstances = new ArrayList();

   public LaunchAttributes (ILaunchConfiguration configuration)
      throws CoreException
   {
      initialize(configuration);
   }
   
   public void initialize (ILaunchConfiguration configuration)
      throws CoreException
   { 
      try {
         consoleConfigurationName = configuration.getAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME,"");        
         outputPath = configuration.getAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR,"");
         reverseEngineer = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, false);
         revengSettings = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, "");
         revengStrategy = configuration.getAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_STRATEGY, "");
         useOwnTemplates = configuration.getAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES,false);
         enableJDK5 = configuration.getAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5,false);
         enableEJB3 = configuration.getAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS,false);
         packageName = configuration.getAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME,"");
         templatePath = configuration.getAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR,"");
         preferBasicCompositeIds = configuration.getAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
         
         loadExporters(configuration);
         
         if (!useOwnTemplates) {
        	 templatePath = null;
         }
         
      } catch (CoreException e) {
         throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666)); 
      }
   }
   
   protected void loadExporters (ILaunchConfiguration configuration)
      throws CoreException
   {
	  ExporterDefinition definitions[] = ExtensionManager.findExporterDefinitions();
      String[] exporterList = configuration.getAttribute(HibernateLaunchConstants.ATTR_EXPORTER_LIST, "").split(",");
      for (int i = 0; i < exporterList.length; i++)
      {
         if (exporterList[i].length() == 0) continue;
         
    	 ExporterInstance instance = new ExporterInstance(findDefinition(definitions, exporterList[i]), exporterList[i]);
    	 
         for (Iterator iter = configuration.getAttributes().keySet().iterator(); iter.hasNext(); )
         {
            String attributeName = (String) iter.next();
            if (attributeName.startsWith(exporterList[i] + "."))
            {
               String propertyName = attributeName.substring(exporterList[i].length() + 1);
               ExporterProperty property = instance.findOrCreateProperty(propertyName);
               
               instance.setProperty(property, configuration.getAttribute(attributeName, ""));
            }
         }
         
         exporterInstances.add(instance);
      }
   }
   
   // Exporter instance IDs should be the exporter definition id + a unique number/id
   protected ExporterDefinition findDefinition (ExporterDefinition definitions[], String instanceId)
   		throws CoreException
   {
	   for (int i = 0; i < definitions.length; i++)
	   {
		   if (instanceId.contains(definitions[i].getId()))
		   {
			   return definitions[i];
		   }
	   }
	   
	   throw new CoreException(new Status(
			   IStatus.ERROR, "org.hibernate.eclipse.console", 666, "Exporter definition for \"" + instanceId + "\" was not found", null));
   }
   
   protected void saveExporters (ILaunchConfigurationWorkingCopy configuration)
	{
	   String exporterList = new String();
	   
	   for (Iterator iter = exporterInstances.iterator(); iter.hasNext(); )
	   { 
		   ExporterInstance instance = (ExporterInstance) iter.next();
		   exporterList += instance.getId();
		   if (iter.hasNext())
		   {
			   exporterList += ",";
		   }
		   
		   for (Iterator propertyIter = instance.getProperties().keySet().iterator(); propertyIter.hasNext(); )
		   {
			   ExporterProperty property = (ExporterProperty) propertyIter.next();
               String attributeName = instance.getId() + "." + property.getName();
               String attributeValue = (String) instance.getProperties().get(property);
               
			   configuration.setAttribute(attributeName, attributeValue);
		   }
	   }
       
       if (exporterList.length() > 0)
          configuration.setAttribute(HibernateLaunchConstants.ATTR_EXPORTER_LIST, exporterList);
	}
   
   public void save (ILaunchConfigurationWorkingCopy configuration)
   {
      configuration.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, consoleConfigurationName);        
      configuration.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, outputPath);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, reverseEngineer);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, revengSettings);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_STRATEGY, revengStrategy);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES, useOwnTemplates);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, enableJDK5);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, enableEJB3);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, packageName);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, templatePath);
      configuration.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, preferBasicCompositeIds);
      
      saveExporters(configuration);
   }
   
   public ExporterInstance createExporterInstance (ExporterDefinition definition)
   {
      int index = 0;
      
      for (Iterator iter = exporterInstances.iterator(); iter.hasNext(); )
      {
         ExporterInstance instance = (ExporterInstance) iter.next();
         if (instance.getDefinition().equals(definition))
         {
            index++;
         }
      }
      
      ExporterInstance instance = new ExporterInstance(definition, definition.getId() + "." + index);
      return instance;
   }
   
   public List getExporterInstances ()
   {
	   return exporterInstances;
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
}

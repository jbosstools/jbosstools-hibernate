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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

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
         
         if (!useOwnTemplates) {
        	 templatePath = null;
         }
         
      } catch (CoreException e) {
         throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666)); 
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

   

   
}

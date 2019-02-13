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

import org.hibernate.console.ConfigurationXMLStrings;
import org.hibernate.eclipse.console.model.impl.ExporterFactoryStrings;

public class HibernateLaunchConstants
{
   public static final String ATTR_PREFIX = "org.hibernate.tools."; //$NON-NLS-1$
   
   // attribute to list of id for exporters configured in a specific launchconfig.
   public static final String ATTR_EXPORTERS = ATTR_PREFIX + "exporters"; //$NON-NLS-1$
   
   public static final String ATTR_CONSOLE_CONFIGURATION_NAME = ATTR_PREFIX + "configurationname"; //$NON-NLS-1$
   public static final String ATTR_OUTPUT_DIR = ATTR_PREFIX + ExporterFactoryStrings.OUTPUTDIR;
   public static final String ATTR_REVERSE_ENGINEER = ATTR_PREFIX + "schema2hbm"; //$NON-NLS-1$
   public static final String ATTR_REVERSE_ENGINEER_SETTINGS = ATTR_PREFIX + ConfigurationXMLStrings.REVENGFILE;
   public static final String ATTR_REVERSE_ENGINEER_STRATEGY = ATTR_PREFIX + "revengstrategy"; //$NON-NLS-1$
   public static final String ATTR_USE_OWN_TEMPLATES = ATTR_PREFIX + "useOwnTemplates"; //$NON-NLS-1$
   public static final String ATTR_USE_EXTERNAL_PROCESS = ATTR_PREFIX + "useExternalProcess"; //$NON-NLS-1$
   public static final String ATTR_ENABLE_EJB3_ANNOTATIONS = ATTR_PREFIX + CodeGenerationStrings.EJB3;
   public static final String ATTR_ENABLE_JDK5 = ATTR_PREFIX + CodeGenerationStrings.JDK5;
   public static final String ATTR_PACKAGE_NAME = ATTR_PREFIX + "package"; //$NON-NLS-1$
   public static final String ATTR_ENABLE_TEMPLATE_DIR = ATTR_PREFIX + "templatepathenabled"; //$NON-NLS-1$
   public static final String ATTR_TEMPLATE_DIR = ATTR_PREFIX + "templatepath"; //$NON-NLS-1$
   public static final String ATTR_PREFER_BASIC_COMPOSITE_IDS = ATTR_PREFIX + "prefercompositeids"; //$NON-NLS-1$
   public static final String ATTR_AUTOMATIC_MANY_TO_MANY = ATTR_PREFIX + "reveng.detect_many_to_many"; //$NON-NLS-1$
   public static final String ATTR_AUTOMATIC_VERSIONING = ATTR_PREFIX + "reveng.detect_optimistc_lock"; //$NON-NLS-1$
   public static final String ATTR_AUTOMATIC_ONE_TO_ONE = ATTR_PREFIX + "reveng.detect_one_to_one"; //$NON-NLS-1$
   public static final String ATTR_REVENG_TABLES = ATTR_PREFIX + "reveng.tables";//$NON-NLS-1$
   
}

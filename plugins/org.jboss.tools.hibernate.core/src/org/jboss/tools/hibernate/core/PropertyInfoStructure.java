/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core;

/**
 * structure to hold field/property parameters description
 */
public class PropertyInfoStructure
{
    /**
     * default constructor
     */
    public PropertyInfoStructure()
    {
        super();
    }
    
    /**
     * @param propertyName name of a field/property
     * @param propertyTypeName fully-qualified name of field/property Java type
     */
    public PropertyInfoStructure(String propertyName, String propertyTypeName)
    {
        super();
        this.propertyName = propertyName;
        this.propertyTypeName = propertyTypeName;
    }
    /**
     * name of a field/property
     */
    public String propertyName;
    /**
     * fully-qualified name of field/property Java type preceded by array signature
     * e.g. <i>[byte, [[java.lang.Object, java.util.Map</i>
     */
    public String propertyTypeName;
}
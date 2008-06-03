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
package org.jboss.tools.hibernate.internal.core.hibernate;

import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;


/**
 * @author Nick
 *
 * Reads default values for parameters of PropertyMapping and returns new PropertyMapping
 * with parameters filled
 */
public class DefaultPropertyMappingFactory {
    
    private IOrmConfiguration configuration;
    
    private boolean isLazy = false; // changed by Nick 20.09.2005
    private boolean isOptimisticLocked;
    private String accessorName;
    private boolean isNotNull;
    
    public DefaultPropertyMappingFactory(IOrmConfiguration ormConfig)
    {
        configuration = ormConfig;
        readDefaultValues();
    }
    
    private void readDefaultValues()
    {
//        String isLazyString = configuration.getProperty(OrmConfiguration.HIBERNATE_LAZY);
//        if (isLazyString == null)
//            isLazyString = OrmConfiguration.DEFAULT_LAZY;
//        isLazy = Boolean.valueOf(isLazyString).booleanValue();

/*        String isOptimisticLockedString = configuration.getProperty(OrmConfiguration.HIBERNATE_OPTIMISTIC);
        if (isOptimisticLockedString == null)
            isOptimisticLockedString = OrmConfiguration.DEFAULT_OPTIMISTIC_STRATEGY;
        isOptimisticLocked = isOptimisticLockedString.equals(OrmConfiguration.CHECK_VERSION);
    
*/
        accessorName = configuration.getProperty(OrmConfiguration.HIBERNATE_ACCESS);
        if (accessorName == null)
            accessorName = OrmConfiguration.DEFAULT_ACCESS;
    
    }	
    
    public PropertyMapping createDefaultPropertyMapping()
    {
        PropertyMapping pm = new PropertyMapping();

        pm.setLazy(isLazy);
        //pm.setOptimisticLocked(isOptimisticLocked); - ORMIISTUD-300
        pm.setOptimisticLocked(true);
        pm.setPropertyAccessorName(accessorName);
        return pm;
    }
}

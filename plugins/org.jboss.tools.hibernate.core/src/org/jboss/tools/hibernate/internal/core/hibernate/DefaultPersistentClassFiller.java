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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmProject;
import org.jboss.tools.hibernate.internal.core.OrmPropertyDescriptorsHolder;

/**
 * @author Nick
 *
 * Reads default values for parameters of PersistentClass and returns new PersistentClass
 * with parameters filled
 */
public class DefaultPersistentClassFiller {
    private boolean safeGetBooleanProperty(String propertyName)
    {
        String propertyString = configuration.getProperty(propertyName);
        boolean result = false;
        try {
            result = Boolean.valueOf(propertyString).booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logInfo("Exception parsing "+propertyName+" property",e);
            configuration.setProperty(propertyName,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(propertyName));
            save = true;
        }
        return result;
    }
    
    private int safeGetIntProperty(String propertyName)
    {
        String propertyString = configuration.getProperty(propertyName);
        int result = 0;
        try {
            result = Integer.parseInt(propertyString);
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing "+propertyName+" property",e);
            configuration.setProperty(propertyName,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(propertyName));
            save = true;
        }
        return result;
    }

    private IOrmConfiguration configuration;

    private boolean dynamic_update;
    private boolean dynamic_insert;
    private boolean select_before_update;
    private int jdbc_batch_size;
    private Boolean isLazy = null;
    private String optimistic_lock_mode;
    
    private boolean discriminator_force;
    private boolean discriminator_insert;
    private OrmPropertyDescriptorsHolder ormPropertyDescriptorsHolder;    
    private boolean save=false;
    
	public DefaultPersistentClassFiller(OrmProject ormProject)
    {
        configuration = ormProject.getOrmConfiguration();
        ormPropertyDescriptorsHolder = OrmPropertyDescriptorsHolder.getInstance(ormProject);
        readDefaultValues();
    }
    
    private void readDefaultValues()
    {
        //XXX Set default values from IOrmConfiguration:

        //XXX Nick 9.03.2005 what property names should be used there to get
        // dynamic-update state etc.?
        //Your names are fine . But there are no such property descriptors in
        // OrmPropertyDescriptorsHolder. You should create property descriptors
        // first
        
        select_before_update = safeGetBooleanProperty(OrmConfiguration.CLASS_SELECT_BEFORE_UPDATE);
        
        //XXX JDBC batch size and class fetching batch size are different
        // things. Plz use property hibernate.class.batch_size.
        jdbc_batch_size = safeGetIntProperty(OrmConfiguration.CLASS_BATCH_SIZE);

        String lazyString = configuration.getProperty(OrmConfiguration.HIBERNATE_LAZY);
        try {
            isLazy = Boolean.valueOf(lazyString);
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing hibernate.lazy",e);
            configuration.setProperty(OrmConfiguration.HIBERNATE_LAZY,
                    (String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HIBERNATE_LAZY));
            save = true;
        }
        
        optimistic_lock_mode = configuration
                .getProperty(OrmConfiguration.HIBERNATE_OPTIMISTIC);   
        //dynamic_update = safeGetBooleanProperty(OrmConfiguration.CLASS_DYNAMIC_UPDATE,OrmConfiguration.DEFAULT_DYNAMIC_UPDATE);
        //dynamic_insert = safeGetBooleanProperty(OrmConfiguration.CLASS_DYNAMIC_INSERT,OrmConfiguration.DEFAULT_DYNAMIC_INSERT);
        boolean dynamic = !OrmConfiguration.CHECK_NONE.equals(optimistic_lock_mode);
        dynamic_update = dynamic;
        dynamic_insert = dynamic;
        
        discriminator_force = safeGetBooleanProperty("hibernate.discriminator.force");
        discriminator_insert = safeGetBooleanProperty("hibernate.discriminator.insert");
        if(save)
        	try {
        		configuration.save();
        		save=false;
        	} catch (Exception e) {
        		OrmCore.getPluginLog().logError(e.getMessage(),e);
        	}
    }
    
    public void fillClassMapping(ClassMapping cm) throws JavaModelException
    {
        if (cm instanceof IRootClassMapping) {
            IRootClassMapping rootCM = (IRootClassMapping) cm;

            rootCM.setDiscriminatorInsertable(discriminator_insert);
            rootCM.setForceDiscriminator(discriminator_force);
        }
        
        cm.setBatchSize(jdbc_batch_size);
        //  cm.setCheck();
        //  cm.setCustomSQLDelete()
        //  cm.setCustomSQLInsert()
        //  cm.setCustomSQLUpdate()

        cm.setDynamic(false);
  
        //already set
        //cm.setClassName()
        //cm.setPersistentClass()
        //cm.setDatabaseTable()

        cm.setDynamicInsert(dynamic_insert);
        cm.setDynamicUpdate(dynamic_update);

        if (isLazy != null)
        cm.setLazy(isLazy.booleanValue());
        //  cm.setLoaderName()
        cm.setOptimisticLockMode(optimistic_lock_mode);
        cm.setSelectBeforeUpdate(select_before_update);

		boolean isAbstract = false;
		IType aType = cm.getPersistentClass().getType();
		if (aType != null)
		    isAbstract = Flags.isAbstract(aType.getFlags());
        cm.setIsAbstract(isAbstract);
        
    }
}

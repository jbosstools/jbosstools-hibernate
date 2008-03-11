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
package org.jboss.tools.hibernate.internal.core.hibernate.descriptors;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PersistentClassPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PersistentFieldPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class OneToOneMappingDescriptorsHolder extends PropertyDescriptorsHolder{
	private static OneToOneMappingDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.OneToOneMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.OneToOneMappingDescriptorsHolder_AdvancedCategory;	

	public static PropertyDescriptorsHolder getInstance(IPersistentClass pc,IMapping mod){
		instance=new OneToOneMappingDescriptorsHolder(pc,mod);
		return instance;
	}
	
	private OneToOneMappingDescriptorsHolder(IPersistentClass pc,IMapping mod){

		PropertyDescriptor pd;
		pd=new PersistentFieldPropertyDescriptor("referencedPropertyName",Messages.OneToOneMappingDescriptorsHolder_ReferencedPropertyNameN, null,pc); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_ReferencedPropertyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		String[] dep={"referencedPropertyName"}; //$NON-NLS-1$
		pd=new PersistentClassPropertyDescriptor("referencedEntityName",Messages.OneToOneMappingDescriptorsHolder_ReferencedEntityNameN, dep, mod,false); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_ReferencedEntityNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		String[] fetch={"join","select"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("fetchMode",Messages.OneToOneMappingDescriptorsHolder_FetchModeN,fetch); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_FetchModeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),fetch[1]);

		pd=new DialogTextPropertyDescriptor("formula",Messages.OneToOneMappingDescriptorsHolder_FormulaN); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_FormulaD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("constrained",Messages.OneToOneMappingDescriptorsHolder_ConstrainedN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_ConstrainedD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
		
		pd=new TextPropertyDescriptor("foreignKeyName",Messages.OneToOneMappingDescriptorsHolder_ForeignKeyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.OneToOneMappingDescriptorsHolder_ForeignKeyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
				
	}

}

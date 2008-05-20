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
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;

/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DiscriminatorPropertyDescriptorsHolder extends
		PropertyDescriptorsHolder {

	private static final String DISCRIMINATOR_CATEGORY=Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorCategory;	
	private static DiscriminatorPropertyDescriptorsHolder instance=new DiscriminatorPropertyDescriptorsHolder();
	public static DiscriminatorPropertyDescriptorsHolder getInstance(){
		return instance;
	}

	
	private DiscriminatorPropertyDescriptorsHolder(){
		
		PropertyDescriptor pd=new ListPropertyDescriptor("forceDiscriminator",Messages.DiscriminatorPropertyDescriptorsHolder_ForceDiscriminatorN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.DiscriminatorPropertyDescriptorsHolder_ForceDiscriminatorD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_FORCE_DISCRIMINATOR);
		
		pd=new ListPropertyDescriptor("discriminatorInsertable",Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorInsertableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorInsertableD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_INSERT_DISCRININATOR);
		
		
		pd=new DialogTextPropertyDescriptor("discriminator.formula",Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorFormulaN); //$NON-NLS-1$
		pd.setDescription(Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorFormulaD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DISCRIMINATOR_FORMULA);
		
		pd=new PropertyDescriptor("discriminatorColumnName",Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorColumnNameN); //$NON-NLS-1$
		pd.setDescription(Messages.DiscriminatorPropertyDescriptorsHolder_DiscriminatorColumnNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
	}	
}

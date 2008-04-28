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
package org.jboss.tools.hibernate.internal.core.data;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ChangebleIntegerPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


public class ColumnPropertyDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	protected static final String GENERAL_CATEGORY=Messages.ColumnPropertyDescriptorsHolder_GeneralCategory;
	public static PropertyDescriptorsHolder getInstance(String sqltype){
		instance=new ColumnPropertyDescriptorsHolder(sqltype);
		return instance;
	}
	
	protected ColumnPropertyDescriptorsHolder(String sqltype){
		PropertyDescriptor pd=new ListPropertyDescriptor("nullable",Messages.ColumnPropertyDescriptorsHolder_NullableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_NullableD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);

		pd=new ListPropertyDescriptor("unique",Messages.ColumnPropertyDescriptorsHolder_UniqueN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_UniqueD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);

		String[] DEPENDENCE_DESCRIPTORS = {"precision"}; //$NON-NLS-1$
		String[] DEPENDENCE_DESCRIPTORS1 = {"scale","precision"}; //$NON-NLS-1$ //$NON-NLS-2$
		String[] DEPENDENCE_DESCRIPTORS2 = {"length"};		 //$NON-NLS-1$
		
		pd=new ChangebleIntegerPropertyDescriptor("length",Messages.ColumnPropertyDescriptorsHolder_LengthN,DEPENDENCE_DESCRIPTORS1,0,true); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_LengthD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
	
		pd=new ChangebleIntegerPropertyDescriptor("precision",Messages.ColumnPropertyDescriptorsHolder_PrecisionN,DEPENDENCE_DESCRIPTORS2,0,true); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_PrecisionD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new ChangebleIntegerPropertyDescriptor("scale",Messages.ColumnPropertyDescriptorsHolder_ScaleN,DEPENDENCE_DESCRIPTORS,-1,false); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_ScaleD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
	
		ArrayList typelist=new ArrayList();
		typelist.addAll(TypeUtils.sqlConstantsMap.values());
		if ((sqltype!=null)&&(sqltype.trim()!="")&&(!typelist.contains(sqltype))) //$NON-NLS-1$
				typelist.add(sqltype);	
		
		String[] types=new String[typelist.size()];
		for(int i=0;i<typelist.size();i++)
			types[i]=(String) typelist.get(i);
		Arrays.sort(types);		
		pd=new EditableListPropertyDescriptor("sqlTypeName",Messages.ColumnPropertyDescriptorsHolder_SqlTypeNameN,types); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_SqlTypeNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new PropertyDescriptor("name",Messages.ColumnPropertyDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new TextPropertyDescriptor("checkConstraint",Messages.ColumnPropertyDescriptorsHolder_CheckConstraintN); //$NON-NLS-1$
		pd.setDescription(Messages.ColumnPropertyDescriptorsHolder_CheckConstraintD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
	}
	
}

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
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.Arrays;
import java.util.Iterator;

import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 2, 2005
 */
public class ProperiesRefPropertyDescriptor extends
		PersistentFieldPropertyDescriptor {

	public ProperiesRefPropertyDescriptor(Object id, String displayName, String[] dependentProperties, IPersistentClass pc) {
		super(id, displayName, dependentProperties, pc);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.PersistentFieldPropertyDescriptor#autochangeValues(java.lang.Object)
	 */
	public void autochangeValues(Object changedValue) {
        String values[];
        Object[] propertyValues;		
		if((changedValue instanceof String)&&(mod!=null)) {
		pc=mod.findClass((String)changedValue);	
		if ((pc!=null)&&(pc.getPersistentClassMapping()!=null)&&(((ClassMapping)pc.getPersistentClassMapping()).getPropertyClosureSpan()>0))
		{
		values=new String[((ClassMapping)pc.getPersistentClassMapping()).getPropertyClosureSpan()+1];
		values[0]="";
		Iterator prop = ((ClassMapping)pc.getPersistentClassMapping()).getPropertyIterator();
		int i=1;
		while (prop.hasNext())
		{
			values[i++]=((IPropertyMapping)prop.next()).getName();
		}
		Arrays.sort(values);	
		propertyValues=values;
		}
		else
		{
			values=new String[1];
			values[0]="";
			propertyValues=values;			
		}
		//Arrays.sort(values);
		setValues(values,propertyValues);
		}
		else if (changedValue==null)
		{
			values=new String[1];
			values[0]="";
			propertyValues=values;			
			setValues(values,propertyValues);			
		}
		
	}

	
}

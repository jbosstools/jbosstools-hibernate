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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetEntry;

public class FieldMapingPropertySheetEntry  extends PropertySheetEntry{

	public FieldMapingPropertySheetEntry(){
		super();
	}

	
	 public CellEditor getEditor(Composite parent) {

		 
		 
		 IPropertyDescriptor discriptor=getDescriptor();
			if(discriptor instanceof AutoChangeblePropertyDescriptor){
				if (((AutoChangeblePropertyDescriptor)discriptor).isNewCellEditor())
				{
					((AutoChangeblePropertyDescriptor)discriptor).setNewCellEditor(true);
					//super.setDescriptor(discriptor);
					super.dispose();
				}
			}

			return super.getEditor(parent);
	    }
}

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

/**
 * @author kaa
 * akuzmin@exadel.com
 * Sep 9, 2005
 */
public class DialogTextPropertyDescriptor extends AdaptablePropertyDescriptor {
	
	public DialogTextPropertyDescriptor(Object id, String displayName){
		super(id,displayName);
	}

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new DialogTextCellEditor(parent);
        return editor; 
    }	
}

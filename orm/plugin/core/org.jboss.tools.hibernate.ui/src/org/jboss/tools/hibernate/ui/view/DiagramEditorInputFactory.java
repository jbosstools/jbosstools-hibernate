/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 *
 * author: Vitali Yemialyanchyk
 */
public class DiagramEditorInputFactory implements IElementFactory {

	public final static String ID_FACTORY =  "org.jboss.tools.hibernate.ui.view.DiagramEditorInputFactory"; //$NON-NLS-1$

	public IAdaptable createElement(IMemento memento) {
        DiagramEditorInput hqlStorageInput = new DiagramEditorInput();
        hqlStorageInput.loadState(memento);
        IAdaptable input = hqlStorageInput;        
        return input; 
	}
}

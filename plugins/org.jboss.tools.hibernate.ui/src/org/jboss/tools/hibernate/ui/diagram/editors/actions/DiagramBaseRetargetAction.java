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
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.RetargetAction;

/**
 * @author Vitali Yemialyanchyk
 */
public class DiagramBaseRetargetAction extends RetargetAction {
	
	public DiagramBaseRetargetAction(final String actionId, final String text,
			final String toolTipText, final ImageDescriptor imgDescriptor) {
		super(actionId, text);
		setToolTipText(toolTipText);
		setImageDescriptor(imgDescriptor);
	}

}

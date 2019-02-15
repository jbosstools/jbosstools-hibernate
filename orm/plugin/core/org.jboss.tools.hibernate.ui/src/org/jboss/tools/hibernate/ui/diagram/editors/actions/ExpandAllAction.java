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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.command.ExpandAllCommand;

/**
 * @author Vitali Yemialyanchyk
 */
public class ExpandAllAction extends DiagramBaseAction {

	public static final String ACTION_ID = "expand_all_id"; //$NON-NLS-1$

	public ExpandAllAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ExpandAllAction_expand_all);
		//setImageDescriptor(ImageDescriptor.createFromFile(
		//		DiagramViewer.class, "icons/export.png"));
	}

	public void run() {
		execute(getCommand());
	}

	public Command getCommand() {
		CompoundCommand cc = new CompoundCommand();
		cc.add(new ExpandAllCommand(getDiagramViewer().getOrmDiagram()));
		return cc;
	}
}

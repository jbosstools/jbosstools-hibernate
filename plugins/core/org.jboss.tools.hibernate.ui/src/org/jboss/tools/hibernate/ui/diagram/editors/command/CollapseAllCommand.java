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
package org.jboss.tools.hibernate.ui.diagram.editors.command;

import java.util.HashMap;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;

/**
 * 
 * @author Vitali Yemialyanchyk
 */
public class CollapseAllCommand extends Command {

	protected HashMap<String, Boolean> elExpState; 
	protected OrmDiagram ormDiagram;
	
	public CollapseAllCommand(OrmDiagram ormDiagram) {
		this.ormDiagram = ormDiagram;
	}
	
	public void execute() {
		elExpState = ormDiagram.getElementsExpState();
		ormDiagram.collapseAll();
	}

	public void undo() {
		ormDiagram.setElementsExpState(elExpState);
	}

	public boolean canUndo() {
		return (elExpState != null);
	}
}

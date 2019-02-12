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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramGuide;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramRuler;

/**
 * 
 */
public class DeleteGuideCommand extends Command {

	private DiagramRuler parent;
	private DiagramGuide guide;
	private Map<OrmShape, Integer> oldParts;

	public DeleteGuideCommand(DiagramGuide guide, DiagramRuler parent) {
		super(DiagramViewerMessages.DeleteGuideCommand_Label);
		this.guide = guide;
		this.parent = parent;
	}

	public boolean canUndo() {
		return true;
	}

	public void execute() {
		oldParts = new HashMap<OrmShape, Integer>(guide.getMap());
		Iterator<OrmShape> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			guide.detachPart(iter.next());
		}
		parent.removeGuide(guide);
	}

	public void undo() {
		parent.addGuide(guide);
		Iterator<OrmShape> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			OrmShape part = iter.next();
			guide.attachPart(part, oldParts.get(part).intValue());
		}
	}
}

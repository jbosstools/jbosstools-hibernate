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

import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramGuide;

/**
 *
 */
public class ChangeGuideCommand extends Command {

	private OrmShape part;
	private DiagramGuide oldGuide, newGuide;
	private int oldAlign, newAlign;
	private boolean horizontal;

	public ChangeGuideCommand(OrmShape part, boolean horizontalGuide) {
		super();
		this.part = part;
		horizontal = horizontalGuide;
	}

	protected void changeGuide(DiagramGuide oldGuide, DiagramGuide newGuide, int newAlignment) {
		if (oldGuide != null && oldGuide != newGuide) {
			oldGuide.detachPart(part);
		}
		// You need to re-attach the part even if the oldGuide and the newGuide are the same
		// because the alignment could have changed
		if (newGuide != null) {
			newGuide.attachPart(part, newAlignment);
		}
	}

	public void execute() {
		// Cache the old values
		oldGuide = horizontal ? part.getHorizontalGuide() : part.getVerticalGuide();
		if (oldGuide != null) {
			oldAlign = oldGuide.getAlignment(part);
		}
		redo();
	}

	public void redo() {
		changeGuide(oldGuide, newGuide, newAlign);
	}

	public void setNewGuide(DiagramGuide guide, int alignment) {
		newGuide = guide;
		newAlign = alignment;
	}

	public void undo() {
		changeGuide(newGuide, oldGuide, oldAlign);
	}

}

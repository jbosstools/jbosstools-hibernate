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

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramGuide;

/**
 * 
 */
public class MoveGuideCommand extends Command {

	private int pDelta;
	private DiagramGuide guide;

	public MoveGuideCommand(DiagramGuide guide, int positionDelta) {
		super(DiagramViewerMessages.MoveGuideCommand_Label);
		this.guide = guide;
		pDelta = positionDelta;
	}

	public void execute() {
		guide.setPosition(guide.getPosition() + pDelta);
		Iterator<OrmShape> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			OrmShape part = iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y += pDelta;
			} else {
				location.x += pDelta;
			}
			part.setLocation(location);
		}
	}

	public void undo() {
		guide.setPosition(guide.getPosition() - pDelta);
		Iterator<OrmShape> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			OrmShape part = (OrmShape)iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y -= pDelta;
			} else {
				location.x -= pDelta;
			}
			part.setLocation(location);
		}
	}

}

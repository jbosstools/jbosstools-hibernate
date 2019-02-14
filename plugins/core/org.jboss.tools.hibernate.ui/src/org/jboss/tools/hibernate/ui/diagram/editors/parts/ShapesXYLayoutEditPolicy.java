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
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.jboss.tools.hibernate.ui.diagram.editors.command.ChangeGuideCommand;
import org.jboss.tools.hibernate.ui.diagram.editors.command.ShapeSetConstraintCommand;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmShape;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramGuide;

/**
 * 
 */
public class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

	public ShapesXYLayoutEditPolicy(XYLayout layout) {
		super();
		setXyLayout(layout);
	}

	protected Command chainGuideAttachmentCommand(
			Request request, OrmShape part, Command cmd, boolean horizontal) {
		
		Command result = cmd;
		// Attach to guide, if one is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int alignment = ((Integer)request.getExtendedData()
					.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
					                : SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			ChangeGuideCommand cgm = new ChangeGuideCommand(part, horizontal);
			cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal), alignment);
			result = result.chain(cgm);
		}
		return result;
	}

	protected DiagramGuide findGuideAt(int pos, boolean horizontal) {
		RulerProvider provider = ((RulerProvider)getHost().getViewer().getProperty(
				horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER 
				: RulerProvider.PROPERTY_HORIZONTAL_RULER));
		return (DiagramGuide)provider.getGuideAt(pos);
	}

	protected Command chainGuideDetachmentCommand(Request request, OrmShape part,
			Command cmd, boolean horizontal) {
		Command result = cmd;
		// Detach from guide, if none is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos == null) {
			result = result.chain(new ChangeGuideCommand(part, horizontal));
		}
		return result;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child,
			Object constraint) {
		if (!(child instanceof OrmShapeEditPart && constraint instanceof Rectangle)) {
			return super.createChangeConstraintCommand(request, child, constraint);
		}
		OrmShape part = (OrmShape)child.getModel();
		Command result = new ShapeSetConstraintCommand(part, request,
				((Rectangle) constraint).getLocation());
		if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) {
			Integer guidePos = (Integer)request.getExtendedData()
					.get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
			if (guidePos != null) {
				result = chainGuideAttachmentCommand(request, part, result, true);
			} else if (part.getHorizontalGuide() != null) {
				// SnapToGuides didn't provide a horizontal guide, but this part is attached
				// to a horizontal guide.  Now we check to see if the part is attached to
				// the guide along the edge being resized.  If that is the case, we need to
				// detach the part from the guide; otherwise, we leave it alone.
				int alignment = part.getHorizontalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.NORTH) != 0) {
					edgeBeingResized = -1;
				} else {
					edgeBeingResized = 1;
				}
				if (alignment == edgeBeingResized) {
					result = result.chain(new ChangeGuideCommand(part, true));
				}
			}
		}
		if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) {
			Integer guidePos = (Integer)request.getExtendedData()
					.get(SnapToGuides.KEY_VERTICAL_GUIDE);
			if (guidePos != null) {
				result = chainGuideAttachmentCommand(request, part, result, false);
			} else if (part.getVerticalGuide() != null) {
				int alignment = part.getVerticalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.WEST) != 0) {
					edgeBeingResized = -1;
				} else {
					edgeBeingResized = 1;
				}
				if (alignment == edgeBeingResized) {
					result = result.chain(new ChangeGuideCommand(part, false));
				}
			}
		}
		if (request.getType().equals(REQ_MOVE_CHILDREN)
				|| request.getType().equals(REQ_ALIGN_CHILDREN)) {
			result = chainGuideAttachmentCommand(request, part, result, true);
			result = chainGuideAttachmentCommand(request, part, result, false);
			result = chainGuideDetachmentCommand(request, part, result, true);
			result = chainGuideDetachmentCommand(request, part, result, false);
		}
		return result;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		return null;
	}

	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new NonResizableEditPolicy();
	}
}

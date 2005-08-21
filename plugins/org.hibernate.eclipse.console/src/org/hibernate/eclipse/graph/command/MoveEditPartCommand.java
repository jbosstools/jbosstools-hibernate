package org.hibernate.eclipse.graph.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class MoveEditPartCommand extends Command {

	private AbstractGraphicalEditPart view;
	private Rectangle oldBounds;
	private Rectangle newBounds;

	public MoveEditPartCommand(AbstractGraphicalEditPart classPart, Rectangle oldBounds, Rectangle newBounds)
	{
		super();
		this.view = classPart;
		this.oldBounds = oldBounds;
		this.newBounds = newBounds;
	}

	public void execute()
	{
		view.getFigure().setBounds(newBounds);
	}

	public void undo()
	{
		view.getFigure().setBounds(oldBounds);
	}

}

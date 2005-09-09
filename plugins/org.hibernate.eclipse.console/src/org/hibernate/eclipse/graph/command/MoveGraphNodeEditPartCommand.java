package org.hibernate.eclipse.graph.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.hibernate.eclipse.graph.model.GraphNode;

public class MoveGraphNodeEditPartCommand extends Command {

	private GraphNode view;
	private Rectangle oldBounds;
	private Rectangle newBounds;

	public MoveGraphNodeEditPartCommand(GraphNode pc, Rectangle oldBounds, Rectangle newBounds)
	{
		super();
		this.view = pc;
		this.oldBounds = oldBounds;
		this.newBounds = newBounds;
	}

	public void execute()
	{
		view.setBounds(newBounds);
	}

	public void undo()
	{
		view.setBounds(oldBounds);
	}

}

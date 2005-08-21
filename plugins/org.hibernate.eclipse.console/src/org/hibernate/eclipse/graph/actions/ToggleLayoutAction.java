package org.hibernate.eclipse.graph.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.hibernate.eclipse.graph.EntityGraphView;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;

public class ToggleLayoutAction extends Action {

	EntityGraphView view;	

	public ToggleLayoutAction(EntityGraphView view)
	{
		super("Automatic Layout", IAction.AS_CHECK_BOX);
		this.view = view;
	}

	public void run()
	{
		if (view != null && view.getConfigurationViewAdapter()!=null) 
		{			
			ConfigurationViewAdapter cva = view.getConfigurationViewAdapter();
			boolean isManual = cva.isManualLayoutDesired();
			cva.setManualLayoutDesired(!isManual);			
			setChecked(!isManual);
		}
	}

	public boolean isChecked()
	{
		if (view != null)
			return isChecked(view);
		else
			return super.isChecked();
	}

	public boolean isChecked(EntityGraphView view)
	{

		if (view != null && view.getConfigurationViewAdapter()!=null)
		{
			boolean checkTrue = view.getConfigurationViewAdapter().isManualLayoutDesired();
			return (!checkTrue);
		}
		else
		{
			return false;
		}

	}
}

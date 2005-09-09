package org.hibernate.eclipse.graph;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.hibernate.cfg.Configuration;
import org.hibernate.eclipse.console.ConsolePreferencesConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.views.KnownConfigurationsView;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

public class EntityGraphView extends AbstractGraphViewPart {

	public static final String ID = EntityGraphView.class.getName();

	public EntityGraphView() {
		super();		
	}

	
	protected void setupListener() {
		getSite().getPage().addSelectionListener(KnownConfigurationsView.ID, listener);
	}

	protected void disposeListeners() {
		getSite().getPage().removeSelectionListener(KnownConfigurationsView.ID, listener);		
	}

	protected void selectionChanged(IStructuredSelection ss)  {
		Object o = ss.getFirstElement();
		if (o instanceof Configuration) {
			viewer.setContents(new ConfigurationViewAdapter((Configuration) o));
			boolean b = HibernateConsolePlugin.getDefault().getPluginPreferences().getBoolean(ConsolePreferencesConstants.ENTITY_MODEL_LAYOUT);
			((ConfigurationEditPart)viewer.getContents()).setManualLayoutActive(b);
		}
	}

	

		
}

	
	

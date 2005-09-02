package org.hibernate.eclipse.graph.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.ConsolePreferencesConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.graph.EntityGraphView;

public class ToggleLayoutAction extends Action {

	EntityGraphView view;	

	public ToggleLayoutAction(EntityGraphView view)
	{
		super("Automatic Layout", IAction.AS_CHECK_BOX);
		this.view = view;
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.LAYOUT));
		setDisabledImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.LAYOUT));
		Preferences prefs = HibernateConsolePlugin.getDefault().getPluginPreferences();
		boolean checked = prefs.getBoolean(ConsolePreferencesConstants.ENTITY_MODEL_LAYOUT);
		valueChanged(!checked, false);
	}

	public void run()
	{
		valueChanged(!isChecked(), true);
	}

	private void valueChanged(boolean value, boolean doStore) {
        setChecked(!value);
        view.setManualLayout(value);
        
        setToolTipText(value ?
			"Manual layout active" :
			"Automatic layout active");
        setDescription(value ?
        		"Enable automatic layout" :
        		"Enable manual layout");
        if (doStore) {
	        Preferences prefs = HibernateConsolePlugin.getDefault().getPluginPreferences();
	        prefs.setValue(ConsolePreferencesConstants.ENTITY_MODEL_LAYOUT, value);
	        HibernateConsolePlugin.getDefault().savePluginPreferences();
        }
    }
}

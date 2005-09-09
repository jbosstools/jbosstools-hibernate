package org.hibernate.eclipse.graph.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.graph.AbstractGraphViewPart;

public class ToggleLayoutAction extends Action {

	final AbstractGraphViewPart view;
	final private String pluginKey;	

	public ToggleLayoutAction(AbstractGraphViewPart view, String pluginKey)
	{
		super("Automatic Layout", IAction.AS_CHECK_BOX);
		this.view = view;
		this.pluginKey = pluginKey;
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.LAYOUT));
		setDisabledImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.LAYOUT));
		Preferences prefs = HibernateConsolePlugin.getDefault().getPluginPreferences();
		boolean checked = prefs.getBoolean(pluginKey);
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
	        prefs.setValue(pluginKey, value);
	        HibernateConsolePlugin.getDefault().savePluginPreferences();
        }
    }
}

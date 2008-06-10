/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.graph.AbstractGraphViewPart;

public class ToggleLayoutAction extends Action {

	final AbstractGraphViewPart view;
	final private String pluginKey;

	public ToggleLayoutAction(AbstractGraphViewPart view, String pluginKey)
	{
		super(HibernateConsoleMessages.ToggleLayoutAction_auto_layout, IAction.AS_CHECK_BOX);
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
			HibernateConsoleMessages.ToggleLayoutAction_manual_layout_active :
			HibernateConsoleMessages.ToggleLayoutAction_auto_layout_active);
        setDescription(value ?
        		HibernateConsoleMessages.ToggleLayoutAction_enable_auto_layout :
        		HibernateConsoleMessages.ToggleLayoutAction_enable_manual_layout);
        if (doStore) {
	        Preferences prefs = HibernateConsolePlugin.getDefault().getPluginPreferences();
	        prefs.setValue(pluginKey, value);
	        HibernateConsolePlugin.getDefault().savePluginPreferences();
        }
    }
}

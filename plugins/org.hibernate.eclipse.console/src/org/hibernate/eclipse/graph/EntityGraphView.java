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

	
	

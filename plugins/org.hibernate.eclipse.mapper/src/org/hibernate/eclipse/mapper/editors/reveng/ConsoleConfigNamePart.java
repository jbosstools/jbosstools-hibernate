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
package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.mapper.MapperMessages;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;
import org.hibernate.util.StringHelper;

public class ConsoleConfigNamePart extends RevEngSectionPart {

	private CCombo text;
	private ReverseEngineeringEditor re;


	public ConsoleConfigNamePart(Composite parent, IManagedForm form, ReverseEngineeringEditor re) {
		super(parent,form);
		this.re = re;
	}

	public boolean setFormInput(IReverseEngineeringDefinition def) {
		if(StringHelper.isEmpty(text.getText())) {
			String initialConfg = ""; //$NON-NLS-1$
			try {
				if (re.getHibernateNature()!=null) {
					initialConfg = re.getHibernateNature().getDefaultConsoleConfigurationName();
				}
			} catch (CoreException e) {
				MapperPlugin.getDefault().getLogger().logException(MapperMessages.ConsoleConfigNamePart_problem_when_trying_to_hibernate_project_info,e);
			}

			text.setText(initialConfg);
		}
		return false;
	}

	public void dispose() {

	}

	Control createClient(IManagedForm form) {
		FormToolkit toolkit = form.getToolkit();
		Composite composite = toolkit.createComposite(getSection());
		composite.setLayout(new GridLayout());
		text = new CCombo(composite, SWT.FLAT);
		text.setEditable(false);
		adaptRecursively(toolkit, text);

		ConsoleConfiguration[] cfg = LaunchHelper.findFilteredSortedConsoleConfigs();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		text.setItems(names);

		return composite;
	}

	protected String getSectionDescription() {
		return MapperMessages.ConsoleConfigNamePart_select_console_configuration;
	}

	protected String getSectionTitle() {
		return MapperMessages.ConsoleConfigNamePart_console_configuration;
	}

	String getConsoleConfigName() {
		return text.getText();
	}

	public void setConsoleConfigName(String name) {
		text.setText( name );
	}
}

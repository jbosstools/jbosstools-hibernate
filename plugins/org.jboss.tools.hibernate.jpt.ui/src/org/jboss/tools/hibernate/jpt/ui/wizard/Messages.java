/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.jpt.ui.wizard.messages"; //$NON-NLS-1$

	public static String GenerateInitWizardPage_title;
	public static String GenerateInitWizardPage_export_to_db;
	public static String GenerateInitWizardPage_autodetect;
	public static String GenerateInitWizardPage_use_console_configuration;
	public static String GenerateInitWizardPage_refresh;
	public static String GenerateInitWizardPage_databaseSettings;
	public static String GenerateInitWizardPage_databaseSettings_connection;
	public static String GenerateInitWizardPage_databaseShema;
	public static String GenerateInitWizardPage_err_msg_select_console_configuration;
	public static String GenerateInitWizardPage_err_msg_select_connection_profile;
	public static String GenerateDdlWizardPage_file_name;
	public static String GenerateDdlWizardPage_err_msg_input_file_name;
	public static String HibernatePropertiesComposite_basic_properties;
	public static String HibernatePropertiesComposite_hibernate;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

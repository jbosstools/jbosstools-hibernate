/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.launch.core.refactoring;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.launch.core.refactoring.messages"; //$NON-NLS-1$
	public static String CodeGenerationConsoleNameChange_error_empty_name;
	public static String CodeGenerationConsoleNameChange_error_null_confi;
	public static String CodeGenerationConsoleNameChange_update;
	public static String ConsoleConfigurationRenameParticipant_change_name;
	public static String ConsoleConfigurationRenameParticipant_name;
	public static String ConsoleConfigurationRenameParticipant_update_code_generations;
	public static String ConsoleConfigurationRenameParticipant_update_project_config;
	public static String ConsoleConfigurationRenameProcessor_name;
	public static String ProjectDefaultConfigurationChange_error_title;
	public static String ProjectDefaultConfigurationChange_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

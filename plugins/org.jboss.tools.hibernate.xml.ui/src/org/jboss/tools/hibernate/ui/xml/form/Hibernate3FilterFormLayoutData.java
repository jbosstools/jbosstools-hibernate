/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.xml.form;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormData;

/**
 * @author glory
 */
public class Hibernate3FilterFormLayoutData {
	static String FILTER_ENTITY = "Hibernate3Filter"; //$NON-NLS-1$
	static String FILTER_FOLDER_ENTITY = "Hibernate3FilterFolder"; //$NON-NLS-1$
	static String FILTERDEF_ENTITY = "Hibernate3Filterdef"; //$NON-NLS-1$
	static String FILTERDEF_FOLDER_ENTITY = "Hibernate3FilterdefFolder"; //$NON-NLS-1$
	static String TYPEDEF_ENTITY = "Hibernate3Typedef"; //$NON-NLS-1$
	
	final static IFormData FILTER_LIST_DEFINITION = new FormData(
		"Filters",
		"", //Description //$NON-NLS-1$
		"Filters", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("name", 100, "name")}, //$NON-NLS-1$
		new String[]{FILTER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFilter") //$NON-NLS-1$
	);

	final static IFormData FILTER_FOLDER_DEFINITION = new FormData(
		"Filters",
		"", //"Description //$NON-NLS-1$
		FILTER_FOLDER_ENTITY,
		new FormAttributeData[]{new FormAttributeData("name", 100, "name")}, //$NON-NLS-1$
		new String[]{FILTER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFilter") //$NON-NLS-1$
	);

	final static IFormData FILTERDEF_FOLDER_DEFINITION = new FormData(
		"Filters",
		"", //"Description //$NON-NLS-1$
		FILTERDEF_FOLDER_ENTITY,
		new FormAttributeData[]{new FormAttributeData("name", 100, "name")}, //$NON-NLS-1$
		new String[]{FILTERDEF_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFilterdef") //$NON-NLS-1$
	);

	private final static IFormData PARAMETERS =	new FormData(
		"Parameters",
		"", //"Description //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("name", 30), new FormAttributeData("type", 70)}, //$NON-NLS-1$ //$NON-NLS-2$
		new String[]{"Hibernate3FilterParam"}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddParam") //$NON-NLS-1$
	);

	private final static IFormData[] FILTERDEF_DEFINITIONS = new IFormData[] {
		new FormData(
			"Filter Definition",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(FILTERDEF_ENTITY)
		),
		PARAMETERS
	};

	static IFormData FILTERDEF_DEFINITION = new FormData(
			FILTERDEF_ENTITY, new String[]{null}, FILTERDEF_DEFINITIONS
	);

	private final static IFormData[] TYPEDEF_DEFINITIONS = new IFormData[] {
		new FormData(
			"Type Definition",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(TYPEDEF_ENTITY)
		),
		PARAMETERS
	};

	static IFormData TYPEDEF_DEFINITION = new FormData(
		TYPEDEF_ENTITY, new String[]{null}, TYPEDEF_DEFINITIONS
	);

}

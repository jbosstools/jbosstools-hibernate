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
public class HibConfig3EventFormLayoutData {
	static String EVENT_ENTITY = "HibConfig3Event"; //$NON-NLS-1$
	static String LISTENER_ENTITY = "HibConfig3Listener"; //$NON-NLS-1$
	
	final static IFormData EVENT_LIST_DEFINITION = new FormData(
		"Events",
		"", //Description //$NON-NLS-1$
		"Events", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("type", 100, "type")}, //$NON-NLS-1$
		new String[]{EVENT_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEvent") //$NON-NLS-1$
	);

	final static IFormData EVENT_FOLDER_DEFINITION = new FormData(
		"Events",
		"", //Description //$NON-NLS-1$
		"HibConfig3EventsFolder", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("type", 100, "type")}, //$NON-NLS-1$
		new String[]{EVENT_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEvent") //$NON-NLS-1$
	);

	final static IFormData LISTENER_LIST_DEFINITION = new FormData(
		"Listeners",
		"", //Description //$NON-NLS-1$
//		"Listeners",
		new FormAttributeData[]{
				new FormAttributeData("class", 70, "class"),  //$NON-NLS-1$
				new FormAttributeData("type", 30, "type")}, //$NON-NLS-1$
		new String[]{LISTENER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddListener") //$NON-NLS-1$
	);

	final static IFormData LISTENER_FOLDER_DEFINITION = new FormData(
		"Listeners",
		"", //"Description //$NON-NLS-1$
		"HibConfig3ListenersFolder", //$NON-NLS-1$
		new FormAttributeData[]{
				new FormAttributeData("class", 70, "class"),  //$NON-NLS-1$
				new FormAttributeData("type", 30, "type")}, //$NON-NLS-1$
		new String[]{LISTENER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddListener") //$NON-NLS-1$
	);
	
	final static IFormData[] EVENT_DEFINITIONS = new IFormData[] {
		new FormData(
			"Event",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(EVENT_ENTITY)
		),
		LISTENER_LIST_DEFINITION,
//		new FormData(
//			"Advanced",
//			"", //"Description
//			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(EVENT_ENTITY)
//		),
	};
	
	final static IFormData EVENT_DEFINITION = new FormData(
		EVENT_ENTITY, new String[]{null}, EVENT_DEFINITIONS
	);

}

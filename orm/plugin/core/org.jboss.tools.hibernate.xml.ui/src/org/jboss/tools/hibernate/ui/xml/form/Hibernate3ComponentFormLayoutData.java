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

import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormData;

/**
 * @author glory
 */
public class Hibernate3ComponentFormLayoutData {
	static String COMPONENT_ENTITY = "Hibernate3Component"; //$NON-NLS-1$
	static String DYNAMIC_COMPONENT_ENTITY = "Hibernate3DynamicComponent"; //$NON-NLS-1$

	final static IFormData[] COMPONENT_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3ComponentFormLayoutData_Component,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(COMPONENT_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ComponentFormLayoutData_Properties, 
				"Hibernate3AttributesFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ComponentFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(COMPONENT_ENTITY)
		),
	};

	static IFormData COMPONENT_DEFINITION = new FormData(
		COMPONENT_ENTITY, new String[]{null}, COMPONENT_DEFINITIONS
	);

	final static IFormData[] DYNAMIC_COMPONENT_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3ComponentFormLayoutData_DynamicComponent,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(DYNAMIC_COMPONENT_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ComponentFormLayoutData_Properties, 
				"Hibernate3AttributesFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		new FormData(
			Messages.Hibernate3ComponentFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(COMPONENT_ENTITY)
		),
	};

	static IFormData DYNAMIC_COMPONENT_DEFINITION = new FormData(
		DYNAMIC_COMPONENT_ENTITY, new String[]{null}, DYNAMIC_COMPONENT_DEFINITIONS
	);

}

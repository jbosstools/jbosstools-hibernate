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

public class Hibernate3CompositeElementFormLayoutData {
	static String ELEMENT_ENTITY = "Hibernate3CompositeElement"; //$NON-NLS-1$
	static String NESTED_ELEMENT_ENTITY = "Hibernate3NestedCompositeElement"; //$NON-NLS-1$

	final static IFormData[] ELEMENT_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3CompositeElementFormLayoutData_CompositeElement,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(ELEMENT_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3CompositeElementFormLayoutData_Properties, 
				"Hibernate3AttributesNestedFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3CompositeElementFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(ELEMENT_ENTITY)
		)
	};

	static IFormData ELEMENT_DEFINITION = new FormData(
		ELEMENT_ENTITY, new String[]{null}, ELEMENT_DEFINITIONS
	);

	final static IFormData[] NESTED_ELEMENT_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3CompositeElementFormLayoutData_CompositeElementNested,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(NESTED_ELEMENT_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3CompositeElementFormLayoutData_Properties, 
				"Hibernate3AttributesNestedFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3CompositeElementFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(NESTED_ELEMENT_ENTITY)
		)
	};

	static IFormData NESTED_ELEMENT_DEFINITION = new FormData(
		NESTED_ELEMENT_ENTITY, new String[]{null}, NESTED_ELEMENT_DEFINITIONS
	);

}

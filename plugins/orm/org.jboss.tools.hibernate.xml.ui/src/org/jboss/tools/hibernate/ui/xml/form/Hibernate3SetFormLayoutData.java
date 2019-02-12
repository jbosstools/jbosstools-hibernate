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
import org.jboss.tools.common.model.ui.forms.IFormAttributeData;
import org.jboss.tools.common.model.ui.forms.IFormData;

public class Hibernate3SetFormLayoutData {
	static String SET_ENTITY = "Hibernate3Set"; //$NON-NLS-1$
	static String BAG_ENTITY = "Hibernate3Bag"; //$NON-NLS-1$

	final static IFormData[] SET_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3SetFormLayoutData_Set,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(SET_ENTITY)
		),
		new FormData(
			Messages.Hibernate3SetFormLayoutData_SetKey,
			"", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3SetFormLayoutData_SetElement,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, Messages.Hibernate3SetFormLayoutData_SetElementKind) //$NON-NLS-1$
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3SetFormLayoutData_SetAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(SET_ENTITY)
		),
	};

	final static IFormData[] BAG_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3SetFormLayoutData_Bag,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(BAG_ENTITY)
		),
		new FormData(
			Messages.Hibernate3SetFormLayoutData_BagKey,
			"", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3SetFormLayoutData_BagElement,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, Messages.Hibernate3SetFormLayoutData_BagElementKind) //$NON-NLS-1$
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3SetFormLayoutData_BagAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(BAG_ENTITY)
		),
	};

	static IFormData SET_DEFINITION = new FormData(
		SET_ENTITY, new String[]{null}, SET_DEFINITIONS
	);

	static IFormData BAG_DEFINITION = new FormData(
		BAG_ENTITY, new String[]{null}, BAG_DEFINITIONS
	);

}

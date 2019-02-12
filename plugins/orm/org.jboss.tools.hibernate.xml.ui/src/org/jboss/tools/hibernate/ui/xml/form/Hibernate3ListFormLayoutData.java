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

public class Hibernate3ListFormLayoutData {
	static String LIST_ENTITY = "Hibernate3List"; //$NON-NLS-1$
	static String ARRAY_ENTITY = "Hibernate3Array"; //$NON-NLS-1$

	final static IFormData[] LIST_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3ListFormLayoutData_List,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(LIST_ENTITY)
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ListKey,
			"", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ListIndex,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("index", 100, Messages.Hibernate3ListFormLayoutData_ListIndexKind) //$NON-NLS-1$
			}
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ListElement,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, Messages.Hibernate3ListFormLayoutData_ListElementKind) //$NON-NLS-1$
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ListAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(LIST_ENTITY)
		),
	};

	final static IFormData[] ARRAY_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3ListFormLayoutData_Array,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(ARRAY_ENTITY)
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ArrayKey,
			"", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ArrayIndex,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("index", 100, Messages.Hibernate3ListFormLayoutData_ArrayIndexKind) //$NON-NLS-1$
			}
		),
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ArrayElement,
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, Messages.Hibernate3ListFormLayoutData_ArrayElementKind) //$NON-NLS-1$
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ListFormLayoutData_ArrayAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(ARRAY_ENTITY)
		),
	};

	static IFormData LIST_DEFINITION = new FormData(
		LIST_ENTITY, new String[]{null}, LIST_DEFINITIONS
	);

	static IFormData ARRAY_DEFINITION = new FormData(
		ARRAY_ENTITY, new String[]{null}, ARRAY_DEFINITIONS
	);

}

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
public class Hibernate3PropertyFormLayoutData {
	static String PROPERTY_ENTITY = "Hibernate3Property"; //$NON-NLS-1$
	final static IFormData[] PROPERTY_DEFINITIONS =	new IFormData[] {
		new FormData(
			Messages.Hibernate3PropertyFormLayoutData_Property,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(PROPERTY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3FormulaFormLayoutData.FORMULA_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3PropertyFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(PROPERTY_ENTITY)
		),
	};

	static IFormData PROPERTY_DEFINITION = new FormData(
		PROPERTY_ENTITY, new String[]{null}, PROPERTY_DEFINITIONS
	);

}

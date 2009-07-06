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
import org.jboss.tools.common.model.ui.forms.GreedyLayoutDataFactory;
import org.jboss.tools.common.model.ui.forms.IFormData;

/**
 * @author glory
 */
public class Hibernate3FormulaFormLayoutData {
	static String FORMULA_ENTITY = "Hibernate3Formula"; //$NON-NLS-1$

	static IFormData FORMULA_LIST_DEFINITION = new FormData(
		Messages.Hibernate3FormulaFormLayoutData_FormulaList,
		"", //"Description //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("value", 100)}, //$NON-NLS-1$
		new String[]{FORMULA_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFormula") //$NON-NLS-1$
	);

	static IFormData FORMULA_DEFINITION = new FormData(
		Messages.Hibernate3FormulaFormLayoutData_Formula,
		"", //"Description //$NON-NLS-1$
		FORMULA_ENTITY,
		new FormAttributeData[]{new FormAttributeData("value", GreedyLayoutDataFactory.getInstance())} //$NON-NLS-1$
	);

}

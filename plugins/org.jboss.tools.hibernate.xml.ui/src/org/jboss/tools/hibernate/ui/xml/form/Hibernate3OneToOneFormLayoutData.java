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
public class Hibernate3OneToOneFormLayoutData {
	static String ONE_TO_ONE_ENTITY = "Hibernate3OneToOne"; //$NON-NLS-1$
	
	final static IFormData[] ONE_TO_ONE_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3OneToOneFormLayoutData_OneToOne,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(ONE_TO_ONE_ENTITY)
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3OneToOneFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(ONE_TO_ONE_ENTITY)
		),
	};

	static IFormData ONE_TO_ONE_DEFINITION = new FormData(
		ONE_TO_ONE_ENTITY, new String[]{null}, ONE_TO_ONE_DEFINITIONS
	);

}

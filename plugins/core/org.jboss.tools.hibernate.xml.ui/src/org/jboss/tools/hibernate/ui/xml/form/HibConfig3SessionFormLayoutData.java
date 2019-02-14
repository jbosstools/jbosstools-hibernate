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
public class HibConfig3SessionFormLayoutData {

	private final static IFormData[] SESSION_FACTORY_DEFINITIONS =
		new IFormData[] {
			new FormData(
				Messages.HibConfig3SessionFormLayoutData_Header,
				"", //"Description //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("HibConfig3SessionFactory") //$NON-NLS-1$
			),
			HibConfig3PropertyFormLayoutData.PROPERTY_LIST_DEFINITION,
			HibConfig3MappingFormLayoutData.MAPPING_LIST_DEFINITION,
		};

	final static IFormData SESSION_FACTORY_FORM_DEFINITION = new FormData(
		"HibConfig3SessionFactory", new String[]{null}, SESSION_FACTORY_DEFINITIONS); //$NON-NLS-1$
}

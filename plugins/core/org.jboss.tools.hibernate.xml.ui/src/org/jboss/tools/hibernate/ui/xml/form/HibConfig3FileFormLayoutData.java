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
public class HibConfig3FileFormLayoutData {

	private final static IFormData[] FILE_DEFINITIONS =
		new IFormData[] {
			new FormData(
				Messages.HibConfig3FileFormLayoutData_Header,
				"", //"Description //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("FileHibConfig3") //$NON-NLS-1$
			),
		};

	final static IFormData FILE_FORM_DEFINITION = new FormData(
		"FileHibConfig3", new String[]{null}, FILE_DEFINITIONS); //$NON-NLS-1$
}

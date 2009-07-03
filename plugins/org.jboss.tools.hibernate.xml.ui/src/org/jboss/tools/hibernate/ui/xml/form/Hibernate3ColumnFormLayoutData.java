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
public class Hibernate3ColumnFormLayoutData {
	
	final static IFormData COLUMN_LIST_DEFINITION =	new FormData(
		"Columns",
		"", //"Description //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("name", 50), new FormAttributeData("sql-type", 50)}, //$NON-NLS-1$ //$NON-NLS-2$
		new String[]{"Hibernate3Column"}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddColumn") //$NON-NLS-1$
	);

}

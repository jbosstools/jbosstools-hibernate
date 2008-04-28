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
public class Hibernate3MetaFormLayoutData {
	static String META_ENTITY = "Hibernate3Meta";
	static String TUPLIZER_ENTITY = "Hibernate3Tuplizer";
	
	final static IFormData META_LIST_DEFINITION = new FormData(
		"Meta",
		"", //Description
		"Meta",
		new FormAttributeData[]{new FormAttributeData("attribute", 30, "attribute"), new FormAttributeData("value", 70, "value")},
		new String[]{META_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMeta")
	);

	final static IFormData META_FOLDER_DEFINITION = new FormData(
		"Meta",
		"", //"Description
		"Hibernate3MetaFolder",
		new FormAttributeData[]{new FormAttributeData("attribute", 30, "attribute"), new FormAttributeData("value", 70, "value")},
		new String[]{META_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMeta")
	);

	final static IFormData TUPLIZER_LIST_DEFINITION = new FormData(
		"Tuplizers",
		"", //"Description
		"Tuplizers",
		new FormAttributeData[]{new FormAttributeData("attribute", 100, "class")},
		new String[]{TUPLIZER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTuplizer")
	);
	final static IFormData TUPLIZER_FOLDER_DEFINITION = new FormData(
		"Tuplizers",
		"", //"Description
		"Hibernate3TuplizerFolder",
		new FormAttributeData[]{new FormAttributeData("attribute", 100, "class")},
		new String[]{TUPLIZER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTuplizer")
	);
}

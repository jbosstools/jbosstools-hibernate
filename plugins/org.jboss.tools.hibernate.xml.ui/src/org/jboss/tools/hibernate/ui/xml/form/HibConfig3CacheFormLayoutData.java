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
public class HibConfig3CacheFormLayoutData {
	static String CLASS_CACHE_ENTITY = "HibConfig3ClassCache";
	static String COLLECTION_CACHE_ENTITY = "HibConfig3CollectionCache";
	
	final static IFormData CACHE_LIST_DEFINITION = new FormData(
		"Caches",
		"", //Description
		"Caches",
		new FormAttributeData[]{new FormAttributeData("item", 100, "item")},
		new String[]{CLASS_CACHE_ENTITY, COLLECTION_CACHE_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddCache")
	);

	final static IFormData CACHE_FOLDER_DEFINITION = new FormData(
		"Caches",
		"", //"Description
		"HibConfig3CachesFolder",
		new FormAttributeData[]{new FormAttributeData("item", 100, "item")},
		new String[]{CLASS_CACHE_ENTITY, COLLECTION_CACHE_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddCache")
	);


}

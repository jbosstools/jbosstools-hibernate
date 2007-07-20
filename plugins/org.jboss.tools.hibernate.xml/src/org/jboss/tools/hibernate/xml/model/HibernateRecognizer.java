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
package org.jboss.tools.hibernate.xml.model;

import org.eclipse.core.runtime.FileLocator;
import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.xml.XMLEntityResolver;

public class HibernateRecognizer implements EntityRecognizer {
	static {
		try {
			XMLEntityResolver.registerPublicEntity(
				HibernateConstants.DOC_PUBLICID_3_0, 
				FileLocator.resolve(HibernateRecognizer.class.getResource("/meta/hibernate-mapping-3.0.dtd")).toString()
			);
			XMLEntityResolver.registerSystemEntity(
				HibernateConstants.DOC_SYSTEMID_3_0, 
				FileLocator.resolve(HibernateRecognizer.class.getResource("/meta/hibernate-mapping-3.0.dtd")).toString()
			);
			XMLEntityResolver.registerPublicEntity(
				HibernateConstants.CFG_DOC_PUBLICID_3_0, 
				FileLocator.resolve(HibernateRecognizer.class.getResource("/meta/hibernate-configuration-3.0.dtd")).toString()
			);
			XMLEntityResolver.registerSystemEntity(
				HibernateConstants.CFG_DOC_SYSTEMID_3_0, 
				FileLocator.resolve(HibernateRecognizer.class.getResource("/meta/hibernate-configuration-3.0.dtd")).toString()
			);
		} catch (Exception e) {}
	}

	public String getEntityName(String ext, String body) {
		
		
		return (body == null || !"xml".equals(ext)) ? null 
				: (body.indexOf("\"" + HibernateConstants.DOC_PUBLICID_3_0 + "\"") >= 0 
				   	|| body.indexOf("\"" + HibernateConstants.DOC_SYSTEMID_3_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIBERNATE_3_0 
				: (body.indexOf("\"" + HibernateConstants.CFG_DOC_PUBLICID_3_0 + "\"") >= 0 
				   	|| body.indexOf("\"" + HibernateConstants.CFG_DOC_SYSTEMID_3_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIB_CONFIG_3_0 
				: null;
	}

}

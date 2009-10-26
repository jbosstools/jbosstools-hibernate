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

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.xml.XMLEntityResolver;

public class HibernateRecognizer implements EntityRecognizer {
	static {
		try {
			XMLEntityResolver.registerPublicEntity(
				HibernateConstants.DOC_PUBLICID_3_0, HibernateRecognizer.class, "/meta/hibernate-mapping-3.0.dtd" //$NON-NLS-1$
			);
			XMLEntityResolver.registerSystemEntity(
				HibernateConstants.DOC_SYSTEMID_3_0, HibernateRecognizer.class, "/meta/hibernate-mapping-3.0.dtd" //$NON-NLS-1$
			);
			XMLEntityResolver.registerPublicEntity(
				HibernateConstants.CFG_DOC_PUBLICID_3_0, HibernateRecognizer.class, "/meta/hibernate-configuration-3.0.dtd" //$NON-NLS-1$
			);
			XMLEntityResolver.registerSystemEntity(
				HibernateConstants.CFG_DOC_SYSTEMID_3_0, HibernateRecognizer.class, "/meta/hibernate-configuration-3.0.dtd" //$NON-NLS-1$
			);
			XMLEntityResolver.registerPublicEntity(
					HibernateConstants.CFG_DOC_PUBLICID_2_0, HibernateRecognizer.class, "/meta/hibernate-configuration-2.0.dtd" //$NON-NLS-1$
				);
			XMLEntityResolver.registerSystemEntity(
				HibernateConstants.CFG_DOC_SYSTEMID_2_0, HibernateRecognizer.class, "/meta/hibernate-configuration-2.0.dtd" //$NON-NLS-1$
			);
		} catch (Exception e) {}
	}

	public String getEntityName(String ext, String body) {
		
		return (body == null || !"xml".equals(ext)) ? null  //$NON-NLS-1$
				: (body.indexOf("\"" + HibernateConstants.DOC_PUBLICID_3_0 + "\"") >= 0  //$NON-NLS-1$ //$NON-NLS-2$
				   	|| body.indexOf("\"" + HibernateConstants.DOC_SYSTEMID_3_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIBERNATE_3_0  //$NON-NLS-1$ //$NON-NLS-2$
				: (body.indexOf("\"" + HibernateConstants.CFG_DOC_PUBLICID_3_0 + "\"") >= 0  //$NON-NLS-1$ //$NON-NLS-2$
				   	|| body.indexOf("\"" + HibernateConstants.CFG_DOC_SYSTEMID_3_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIB_CONFIG_3_0 //$NON-NLS-1$ //$NON-NLS-2$
				//we do not support cfg 2.0 but let's try and do the best
				: (body.indexOf("\"" + HibernateConstants.CFG_DOC_PUBLICID_2_0 + "\"") >= 0  //$NON-NLS-1$ //$NON-NLS-2$
				   	|| body.indexOf("\"" + HibernateConstants.CFG_DOC_SYSTEMID_2_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIB_CONFIG_3_0 //$NON-NLS-1$ //$NON-NLS-2$
				: (body.indexOf("\"" + HibernateConstants.RVE_DOC_PUBLICID_3_0 + "\"") >= 0  //$NON-NLS-1$ //$NON-NLS-2$
				   	|| body.indexOf("\"" + HibernateConstants.RVE_DOC_SYSTEMID_3_0 + "\"") >= 0) ? HibernateConstants.ENTITY_FILE_HIB_REV_ENG_3_0 //$NON-NLS-1$ //$NON-NLS-2$
				: null;
	}

}

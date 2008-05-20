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
package org.jboss.tools.hibernate.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.ReplaceWithNewHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.hibernate.xml.model.helpers.ColumnsStructureHelper;


public class AddHibernateManyToAnyHandler extends ReplaceWithNewHandler {

	protected XModelObject modifyCreatedObject(XModelObject o) {
		Properties p = extractProperties(data[0]);
		String[] cs = new String[]{p.getProperty("column1"), p.getProperty("column2")};
		for (int i = 0; i < cs.length; i++) {
			o.addChild(ColumnsStructureHelper.newColumn(o.getModel(), cs[i]));
		}		
		return o;
	}

}

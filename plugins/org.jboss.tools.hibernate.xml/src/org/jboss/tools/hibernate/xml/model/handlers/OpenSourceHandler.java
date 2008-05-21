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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.java.handlers.OpenJavaSourceHandler;

public class OpenSourceHandler extends OpenJavaSourceHandler {

    protected String getType(XModelObject object) {
    	String v = object.getAttributeValue(getAttribute());
    	if(v != null && v.indexOf('.') >= 0) return v;
    	XModelObject f = object;
    	while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
    	if(f == null) return v;
    	String p = f.getAttributeValue("package");
    	if(p != null && p.length() > 0) return p + "." + v;
    	return v;
    }

    protected String getAttribute() {
        return action.getProperty("attribute");
    }

}

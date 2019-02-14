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

import java.util.StringTokenizer;

import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;

public class GrantActionsConstraint extends XAttributeConstraintAList {
	static String WILD_CARD = "*"; //$NON-NLS-1$
	static String MESSAGE = "value must be * or combination of read,insert,update,delete"; //$NON-NLS-1$

    public boolean accepts(String value) {
    	if(value.indexOf(WILD_CARD) >= 0 && !value.equals(WILD_CARD)) {
    		return false;
    	}
        StringTokenizer st = new StringTokenizer(value, ","); //$NON-NLS-1$
        while(st.hasMoreTokens()) {
            String t = st.nextToken().trim();
            if(!values.contains(t)) return false;
        }
        return true;
    }

    public String getError(String value) {
        return accepts(value) ? null : MESSAGE;
    }

}

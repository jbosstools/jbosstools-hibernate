 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.xml.model.constraint;

import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintList;

/**
 * @author Viacheslav Kabanovich
 */
public class ListELConstraint extends XAttributeConstraintList {

    public boolean accepts(String value) {
    	if(ELConstraint.getInstance().accepts(value)) {
    		return true;
    	}
        return super.accepts(value);
    }

    public String getError(String value) {
    	if(accepts(value)) return null;
    	String error = ELConstraint.getInstance().getError(value);
    	if(error != null) {
    		return error;
    	}
        return super.getError(value);
    }

}

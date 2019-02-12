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

import org.jboss.tools.common.meta.constraint.XAttributeConstraint;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintImpl;

/**
 * @author Viacheslav Kabanovich
 */
public class ELConstraint extends XAttributeConstraintImpl {
	public static ELConstraint INSTANCE = new ELConstraint();

	public static XAttributeConstraint getInstance() {
		return INSTANCE;
	}

    public boolean accepts(String value) {
    	if(value != null) {
    		if((value.startsWith("#{") || value.startsWith("${")) && value.endsWith("}")) {
    			return true;
    		}
    		if(value.length() >= 2 && value.startsWith("@") && value.endsWith("@")) {
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Returns not null only if value starts with EL tokens.
     */
    public String getError(String value) {
    	if(accepts(value)) return null;
    	if(value.startsWith("#{") || value.startsWith("${")) {
    		return "value is not a correct EL."; 
    	}
    	if(value.startsWith("@")) {
    		return "value is not a correct property."; 
    	}
    	
        return super.getError(value);
    }

}

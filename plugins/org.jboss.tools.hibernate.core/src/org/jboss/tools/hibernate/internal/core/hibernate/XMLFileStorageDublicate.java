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
package org.jboss.tools.hibernate.internal.core.hibernate;

// create tau 04.10.2005
public class XMLFileStorageDublicate {

    private static ThreadLocal ErrorDublicate = new ThreadLocal() {
    	/* del tau 05.10.2005
        protected synchronized Object initialValue() {
            return new Boolean(false);
        }
        */
    };

    public static String get() {
        return ((String) (ErrorDublicate.get()));
    }
	
    public static void set(String fileName) {
        ErrorDublicate.set(fileName);
    }    

}

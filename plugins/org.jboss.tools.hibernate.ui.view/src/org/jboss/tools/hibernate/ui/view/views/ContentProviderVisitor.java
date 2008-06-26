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
package org.jboss.tools.hibernate.ui.view.views;

import java.util.ResourceBundle;

import org.hibernate.mapping.Column;


public class ContentProviderVisitor {
	
	private static final Object[] nullChildren = new Object[0];

    private ResourceBundle BUNDLE = ResourceBundle.getBundle(ContentProviderVisitor.class.getPackage().getName() + ".views");
	
	public Object visitDatabaseColumn(Column column, Object argument) {
		return null;
	}

}

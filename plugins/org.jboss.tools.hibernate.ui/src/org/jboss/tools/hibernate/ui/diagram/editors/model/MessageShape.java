/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.model;

/**
*
* author: Vitali Yemialyanchyk
*/
public class MessageShape extends OrmShape {

	protected MessageShape(String error) {
		super(error);
	}

	@Override
	public String getKey() {
		return null;
	}

}

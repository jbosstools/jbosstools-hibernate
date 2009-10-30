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
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.data;

import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;

/**
 * @author some modifications from Vitali
 */
public class LinkInfo implements ILinkInfo {

	protected Connection link = null;

	protected String id = null;

	/**
	 * 
	 * @param link
	 */
	public LinkInfo(Connection link) {
		this.link = link;
	}

	/**
	 * 
	 * @param id
	 */
	public LinkInfo(String id) {
		this.id = id;
	}

	public String getTargetID() {
		if (id != null) {
			return id;
		}
		if (link.getTarget() != null) {
			return link.getTarget().toString();
		}
		return ""; //$NON-NLS-1$
	}
}

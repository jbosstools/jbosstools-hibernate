/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.xml.editor;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.jboss.tools.common.model.ui.attribute.adapter.PropertiesContentProposalProvider;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class HibernatePropertiesContentProposalProvider extends PropertiesContentProposalProvider {

	public IContentProposal[] getProposals(String contents, int position) {
		return null;
	}

}

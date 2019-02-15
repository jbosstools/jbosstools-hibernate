/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.console.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * @author Dmitry Geraskov
 *
 */
public class CloseAllQueryPageAction extends Action {

	public CloseAllQueryPageAction() {
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE_ALL) );
		setDisabledImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLOSE_ALL_DISABLED) );
		
		setToolTipText("Close all query pages");		//$NON-NLS-1$
	}

	@Override
	public void run() {
		List<QueryPage> pages = KnownConfigurations.getInstance().getQueryPageModel().getPagesAsList();
		for (int i = 0; i < pages.size(); i++ ) {
			KnownConfigurations.getInstance().getQueryPageModel().remove( (QueryPage) pages.get(i) );
		}
	}

}

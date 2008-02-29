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

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
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
		
		setToolTipText("Close all query pages");
	}

	@Override
	public void run() {
		for (Iterator i = KnownConfigurations.getInstance().getQueryPageModel().getPages(); i.hasNext(); ) {
			KnownConfigurations.getInstance().getQueryPageModel().remove( (QueryPage) i.next() );
		}
	}

}

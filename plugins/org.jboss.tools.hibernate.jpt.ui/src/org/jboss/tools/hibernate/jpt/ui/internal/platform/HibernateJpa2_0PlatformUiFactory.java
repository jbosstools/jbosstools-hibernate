/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import org.eclipse.jpt.jpa.ui.JpaPlatformUi;
import org.eclipse.jpt.jpa.ui.JpaPlatformUiFactory;
import org.eclipse.jpt.jpa.ui.internal.platform.generic.GenericNavigatorProvider;
import org.jboss.tools.hibernate.jpt.ui.internal.HibernateJpa2_0PlatformUiProvider;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpa2_0PlatformUiFactory implements JpaPlatformUiFactory {

	public HibernateJpa2_0PlatformUiFactory() {
		super();
	}

	public JpaPlatformUi buildJpaPlatformUi() {
		return new HibernateJpaPlatformUi(
			new GenericNavigatorProvider(),
			HibernateJpa2_0PlatformUiProvider.instance()
		);
	}

}

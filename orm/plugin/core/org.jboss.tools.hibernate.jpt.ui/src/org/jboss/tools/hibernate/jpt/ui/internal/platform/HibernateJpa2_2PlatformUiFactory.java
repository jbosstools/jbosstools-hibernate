/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import org.eclipse.jpt.jpa.ui.JpaPlatformUi;
import org.eclipse.jpt.jpa.ui.JpaPlatformUiFactory;
import org.eclipse.jpt.jpa.ui.internal.platform.generic.GenericJpaPlatformUiFactory;
import org.jboss.tools.hibernate.jpt.ui.internal.HibernateJpa2_2PlatformUiProvider;

/**
 * @author Koen Aers, jkopriva@redhat.com
 *
 */
public class HibernateJpa2_2PlatformUiFactory implements JpaPlatformUiFactory {

	public HibernateJpa2_2PlatformUiFactory() {
		super();
	}

	public JpaPlatformUi buildJpaPlatformUi() {
		return new HibernateJpaPlatformUi(
			GenericJpaPlatformUiFactory.NAVIGATOR_FACTORY_PROVIDER,
			HibernateJpa2_2PlatformUiProvider.instance()
		);
	}

}

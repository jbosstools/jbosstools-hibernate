/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import org.eclipse.jpt.ui.JpaPlatformUi;
import org.eclipse.jpt.ui.JpaPlatformUiFactory;
import org.eclipse.jpt.ui.internal.GenericJpaPlatformUiProvider;
import org.eclipse.jpt.ui.internal.platform.generic.GenericNavigatorProvider;
import org.eclipse.jpt.ui.internal.structure.JavaResourceModelStructureProvider;
import org.eclipse.jpt.ui.internal.structure.PersistenceResourceModelStructureProvider;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJpaPlatformUiFactory implements JpaPlatformUiFactory {

	public HibernateJpaPlatformUiFactory() {
		super();
	}

	public JpaPlatformUi buildJpaPlatformUi() {
		return new HibernateJpaPlatformUi(
			new HibernateJpaUiFactory(),
			new GenericNavigatorProvider(),
			JavaResourceModelStructureProvider.instance(), 
			PersistenceResourceModelStructureProvider.instance(),
			GenericJpaPlatformUiProvider.instance()
		);
	}

}

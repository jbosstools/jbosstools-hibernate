/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.details.java;

import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaPrimaryKeyJoinColumnsComposite;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateAbstractInheritanceComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaInheritanceComposite extends HibernateAbstractInheritanceComposite<HibernateJavaEntity> {

	@SuppressWarnings("unchecked")
	public HibernateJavaInheritanceComposite(Pane<? extends JavaEntity> parentPane,
	                            Composite parent) {
		super((Pane<? extends HibernateJavaEntity>) parentPane, parent);
	}

	protected void addPrimaryKeyJoinColumnsComposite(Composite container) {
		new JavaPrimaryKeyJoinColumnsComposite(this, container);
	}
}

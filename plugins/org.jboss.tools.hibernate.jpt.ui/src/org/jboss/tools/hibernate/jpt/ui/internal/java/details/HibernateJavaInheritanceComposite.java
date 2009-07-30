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
package org.jboss.tools.hibernate.jpt.ui.internal.java.details;

import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.ui.internal.java.details.JavaPrimaryKeyJoinColumnsComposite;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateAbstractInheritanceComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaInheritanceComposite extends HibernateAbstractInheritanceComposite<HibernateJavaEntity> {

	@SuppressWarnings("unchecked")
	public HibernateJavaInheritanceComposite(FormPane<? extends JavaEntity> parentPane,
	                            Composite parent) {
		super((FormPane<? extends HibernateJavaEntity>) parentPane, parent);
	}

	protected void addPrimaryKeyJoinColumnsComposite(Composite container) {
		new JavaPrimaryKeyJoinColumnsComposite(this, container);
	}
}

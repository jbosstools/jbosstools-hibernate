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
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedQueryPropertyComposite extends HibernateQueryPropertyComposite<HibernateNamedQuery> {

	/**
	 * Creates a new <code>HibernateNamedQueryPropertyComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 */
	public HibernateNamedQueryPropertyComposite(Pane<?> parentPane,
	                                   PropertyValueModel<? extends HibernateNamedQuery> subjectHolder,
	                                   Composite parent) {

		super(parentPane, subjectHolder, parent);
	}

}

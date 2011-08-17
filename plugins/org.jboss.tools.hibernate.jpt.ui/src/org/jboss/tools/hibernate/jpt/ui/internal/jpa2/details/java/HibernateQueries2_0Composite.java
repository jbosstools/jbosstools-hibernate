/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.java;

import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.Query;
import org.eclipse.jpt.jpa.core.jpa2.context.NamedQuery2_0;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.NamedQueryProperty2_0Composite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateQueries2_0Composite extends HibernateQueriesComposite {

	public HibernateQueries2_0Composite(
			Pane<?> parentPane, 
			PropertyValueModel<? extends HibernateJavaQueryContainer> subjectHolder,
			Composite parent) {
		
		super(parentPane, subjectHolder, parent);
	}
	
	@Override
	protected Pane<NamedQuery2_0> buildNamedQueryPropertyComposite(PageBook pageBook) {
		return new NamedQueryProperty2_0Composite(
			this,
			this.buildNamedQuery2_0Holder(),
			pageBook);
	}
	
	protected PropertyValueModel<NamedQuery2_0> buildNamedQuery2_0Holder() {
		return new TransformationPropertyValueModel<Query, NamedQuery2_0>(this.getQueryHolder()) {
			@Override
			protected NamedQuery2_0 transform_(Query value) {
				return (value instanceof NamedQuery2_0) ? (NamedQuery2_0) value : null;
			}
		};
	}
	
}

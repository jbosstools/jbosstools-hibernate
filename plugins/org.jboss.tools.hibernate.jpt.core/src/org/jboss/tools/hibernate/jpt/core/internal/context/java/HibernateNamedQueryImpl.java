/*******************************************************************************
 * Copyright (c) 2009-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.eclipse.jpt.jpa.core.jpql.JpaJpqlQueryHelper;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedQueryImpl extends AbstractHibernateNamedQueryImpl<HibernateNamedQueryAnnotation> implements HibernateJavaNamedQuery {

	public HibernateNamedQueryImpl(JpaContextNode parent,
			HibernateNamedQueryAnnotation queryAnnotation) {
		super(parent, queryAnnotation);
	}
	
	// ********** metadata conversion *********
	@Override
	public void delete() {
		this.getParent().removeHibernateNamedQuery(this);
	}
	
	// ********** validation **********

	@Override
	protected void validateQuery_(JpaJpqlQueryHelper queryHelper, List<IMessage> messages, IReporter reporter) {
//		queryHelper.validate(this, this.query, this.queryAnnotation.getQueryTextRanges(), 1, messages);
		queryHelper.validate(this, this.query, this.query, this.queryAnnotation.getQueryTextRanges(), 1, null, messages);
	}

	// ********** misc **********
	@Override
	public Class<HibernateNamedQuery> getType() {
		return HibernateNamedQuery.class;
	}
}

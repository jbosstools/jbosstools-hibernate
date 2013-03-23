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

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
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

	public HibernateNamedQueryImpl(JavaQueryContainer parent,
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
	public void validate(JpaJpqlQueryHelper queryHelper, List<IMessage> messages, IReporter reporter) {
//		queryHelper.validate(this, this.query, this.queryAnnotation.getQueryTextRanges(), 1, messages);
		queryHelper.validate(this, this.queryAnnotation.getQuery(), this.queryAnnotation.getQuery(), this.queryAnnotation.getQueryTextRanges(), 1, null, messages);
	}

	// ********** misc **********
	@Override
	public Class<HibernateNamedQuery> getType() {
		return HibernateNamedQuery.class;
	}

	@Override
	public String getQuery() {
		return this.queryAnnotation.getQuery();
	}

	@Override
	public void setQuery(String query) {
		this.queryAnnotation.setQuery(query);
	}

	@Override
	public List<TextRange> getQueryTextRanges() {
		// TODO Auto-generated method stub
		return null;
	}
}

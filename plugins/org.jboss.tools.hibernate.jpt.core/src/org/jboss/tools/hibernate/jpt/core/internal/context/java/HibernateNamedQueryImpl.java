/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.jpql.JpaJpqlQueryHelper;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedQueryImpl extends AbstractHibernateNamedQueryImpl<HibernateNamedQueryAnnotation> implements HibernateJavaNamedQuery {

	public HibernateNamedQueryImpl(JavaJpaContextNode parent,
			HibernateNamedQueryAnnotation queryAnnotation) {
		super(parent, queryAnnotation);
	}
	
	// ********** validation **********

	@Override
	protected void validateQuery_(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		JpaJpqlQueryHelper helper = new JpaJpqlQueryHelper();
		helper.validate(this, this.query, this.getQueryAnnotation().getQueryTextRange(astRoot), 1, messages);
	}

}

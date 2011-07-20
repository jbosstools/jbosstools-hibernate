/*******************************************************************************
 * Copyright (c) 2008-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.jpt.jpa.core.context.java.JavaEmbeddable;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.resource.java.EmbeddableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEmbeddable;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaFactory extends HibernateAbstractJpaFactory {

	// ********** Hibernate Specific **********

	@Override
	public JavaEmbeddable buildJavaEmbeddable(JavaPersistentType parent, EmbeddableAnnotation embeddableAnnotation) {
		return new HibernateJavaEmbeddable(parent, embeddableAnnotation);
	}

}

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

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.GenericJavaManyToOneRelationshipReference;
import org.eclipse.jpt.core.jpa2.context.java.JavaManyToOneRelationshipReference2_0;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaManyToOneMapping extends AbstractHibernateJavaManyToOneMapping {

	public HibernateJavaManyToOneMapping(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	@Override
	protected JavaManyToOneRelationshipReference2_0 buildRelationshipReference() {
		return new GenericJavaManyToOneRelationshipReference(this);
	}	

}

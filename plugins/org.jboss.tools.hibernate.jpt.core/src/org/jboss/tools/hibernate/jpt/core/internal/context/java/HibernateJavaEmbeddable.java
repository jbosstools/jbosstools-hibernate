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

import org.eclipse.jpt.jpa.core.MappingKeys;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaEmbeddable;
import org.eclipse.jpt.jpa.core.resource.java.EmbeddableAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaEmbeddable extends AbstractJavaEmbeddable
{
	public HibernateJavaEmbeddable(JavaPersistentType parent, EmbeddableAnnotation mappingAnnotation) {
		super(parent, mappingAnnotation);
	}

	@Override
	public boolean attributeMappingKeyAllowed(String attributeMappingKey) {
		//hibernate  allows basic, transient and many-to-one within an Embeddable
		return attributeMappingKey == MappingKeys.BASIC_ATTRIBUTE_MAPPING_KEY
			|| attributeMappingKey == MappingKeys.TRANSIENT_ATTRIBUTE_MAPPING_KEY
			|| attributeMappingKey == MappingKeys.MANY_TO_ONE_ATTRIBUTE_MAPPING_KEY;
	}
}

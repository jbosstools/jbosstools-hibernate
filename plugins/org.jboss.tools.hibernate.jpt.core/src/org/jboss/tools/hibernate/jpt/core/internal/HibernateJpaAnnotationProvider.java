/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.Collection;

import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotationImpl.GenericGeneratorAnnotationDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaAnnotationProvider extends GenericJpaAnnotationProvider {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider#addAttributeAnnotationDefinitionsTo(java.util.Collection)
	 */
	@Override
	protected void addAttributeAnnotationDefinitionsTo(Collection<AnnotationDefinition> definitions) {
		super.addAttributeAnnotationDefinitionsTo(definitions);
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
	}

}

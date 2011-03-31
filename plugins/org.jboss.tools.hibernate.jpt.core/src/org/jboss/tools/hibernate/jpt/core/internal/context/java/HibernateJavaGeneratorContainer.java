/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.ListIterator;

import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public interface HibernateJavaGeneratorContainer extends
		HibernateGeneratorContainer, JavaGeneratorContainer {
	
	ListIterator<JavaGenericGenerator> genericGenerators();
	
	JavaGenericGenerator addGenericGenerator(int index);

}

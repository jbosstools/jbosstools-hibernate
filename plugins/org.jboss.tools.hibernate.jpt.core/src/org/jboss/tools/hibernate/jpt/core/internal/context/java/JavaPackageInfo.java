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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourcePackage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * When <class>test.some.pack</class> is used and there is
 * test.some.pack.package-info.java file it should be included in persistent unit.
 *
 * @author Dmitry Geraskov
 *
 */
public interface JavaPackageInfo extends JavaJpaContextNode, JpaContextNode, JpaStructureNode {

	// ********** name **********

	/**
	 * Return the persistent type's [fully-qualified] name.
	 * The enclosing type separator is <code>'.'</code>,
	 * as opposed to <code>'$'</code>.
	 * @see #getSimpleName()
	 */
	String getName();
		String NAME_PROPERTY = "name"; //$NON-NLS-1$
		
	JavaResourcePackage getResourcePackage();

	// ********** validation **********

	/**
	 * Add to the list of current validation messages
	 */
	void validate(List<IMessage> messages, IReporter reporter);

	/**
	 * Return the text range to be used with any validation messages related
	 * to the persistent type.
	 */
	TextRange getValidationTextRange();
	
}

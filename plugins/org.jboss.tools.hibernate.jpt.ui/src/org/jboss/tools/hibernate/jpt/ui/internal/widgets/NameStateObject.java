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
package org.jboss.tools.hibernate.jpt.ui.internal.widgets;

import java.util.Collection;
import java.util.List;


import org.eclipse.jpt.utility.internal.StringTools;
import org.eclipse.jpt.utility.internal.node.AbstractNode;
import org.eclipse.jpt.utility.internal.node.Node;
import org.eclipse.jpt.utility.internal.node.Problem;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateUIMappingMessages;

/**
 * @author Dmitry Geraskov
 *
 */
final class NameStateObject extends AbstractNode
{
	/**
	 * The initial name or <code>null</code>
	 */
	private String name;
	
	/**
	 * The collection of names that can't be used or an empty collection if none
	 * are available.
	 */
	private Collection<String> names;

	/**
	 * The <code>Validator</code> used to validate this state object.
	 */
	private Validator validator;

	/**
	 * Notifies a change in the data value property.
	 */
	static final String NAME_PROPERTY = "nameProperty"; //$NON-NLS-1$

	/**
	 * Creates a new <code>NameStateObject</code>.
	 *
	 * @param name The initial input or <code>null</code> if no initial value can
	 * be specified
	 * @param names The collection of names that can't be used or an empty
	 * collection if none are available
	 */
	NameStateObject(String name, Collection<String> names) {
		super(null);
		this.name = name;
		this.names = names;
	}

	private void addNameProblemsTo(List<Problem> currentProblems) {
		if (StringTools.stringIsEmpty(this.name)) {
			currentProblems.add(buildProblem(HibernateUIMappingMessages.NameStateObject_nameMustBeSpecified));
		} else if (names != null && names.contains(name.trim())) {
			currentProblems.add(buildProblem(HibernateUIMappingMessages.NameStateObject_nameAlreadyExists));
		}
	}

	@Override
	protected void addProblemsTo(List<Problem> currentProblems) {
		super.addProblemsTo(currentProblems);
		addNameProblemsTo(currentProblems);
	}

	@Override
	protected void checkParent(Node parentNode) {
		//no parent
	}

	public String displayString() {
		return null;
	}

	String getName() {
		return this.name;
	}

	public void setName(String newName) {
		String oldName = this.name;
		this.name = newName;
		firePropertyChanged(NAME_PROPERTY, oldName, newName);
	}

	@Override
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Validator getValidator() {
		return this.validator;
	}
}
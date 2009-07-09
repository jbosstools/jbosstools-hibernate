/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateSourceNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateSourceNamedQueriesAnnotation extends SourceAnnotation<Member> 
implements HibernateNamedQueriesAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<HibernateNamedQueryAnnotation> hibernateNamedQueries = new Vector<HibernateNamedQueryAnnotation>();


	public HibernateSourceNamedQueriesAnnotation(JavaResourceNode parent, Member member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		AnnotationContainerTools.initialize(this, astRoot);
	}

	public void update(CompilationUnit astRoot) {
		AnnotationContainerTools.update(this, astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.hibernateNamedQueries);
	}
	
	// ********** AnnotationContainer implementation **********

	public String getContainerAnnotationName() {
		return this.getAnnotationName();
	}

	public org.eclipse.jdt.core.dom.Annotation getContainerJdtAnnotation(CompilationUnit astRoot) {
		return this.getJdtAnnotation(astRoot);
	}

	public String getElementName() {
		return Hibernate.NAMED_QUERIES__VALUE;
	}

	public String getNestableAnnotationName() {
		return HibernateNamedQueryAnnotation.ANNOTATION_NAME;
	}

	public ListIterator<HibernateNamedQueryAnnotation> nestedAnnotations() {
		return new CloneListIterator<HibernateNamedQueryAnnotation>(this.hibernateNamedQueries);
	}

	public int nestedAnnotationsSize() {
		return this.hibernateNamedQueries.size();
	}

	public HibernateNamedQueryAnnotation addNestedAnnotationInternal() {
		HibernateNamedQueryAnnotation namedQuery = this.buildHibernateNamedQuery(this.hibernateNamedQueries.size());
		this.hibernateNamedQueries.add(namedQuery);
		return namedQuery;
	}

	private HibernateNamedQueryAnnotation buildHibernateNamedQuery(int index) {
		return HibernateSourceNamedQueryAnnotation.createNestedHibernateNamedQuery(this, member, index, this.daa);
	}
	
	public void nestedAnnotationAdded(int index, HibernateNamedQueryAnnotation nestedAnnotation) {
		this.fireItemAdded(HIBERNATE_NAMED_QUERIES_LIST, index, nestedAnnotation);
	}

	public HibernateNamedQueryAnnotation moveNestedAnnotationInternal(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.hibernateNamedQueries, targetIndex, sourceIndex).get(targetIndex);
	}

	public void nestedAnnotationMoved(int targetIndex, int sourceIndex) {
		this.fireItemMoved(HIBERNATE_NAMED_QUERIES_LIST, targetIndex, sourceIndex);
	}

	public HibernateNamedQueryAnnotation removeNestedAnnotationInternal(int index) {
		return this.hibernateNamedQueries.remove(index);
	}

	public void nestedAnnotationRemoved(int index, HibernateNamedQueryAnnotation nestedAnnotation) {
		this.fireItemRemoved(HIBERNATE_NAMED_QUERIES_LIST, index, nestedAnnotation);
	}


}

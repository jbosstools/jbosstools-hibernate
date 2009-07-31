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

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateSourceNamedNativeQueriesAnnotation extends SourceAnnotation<Member> implements
		HibernateNamedNativeQueriesAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<HibernateNamedNativeQueryAnnotation> hibernateNamedNativeQueries = new Vector<HibernateNamedNativeQueryAnnotation>();


	public HibernateSourceNamedNativeQueriesAnnotation(JavaResourceNode parent, Member member) {
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
		sb.append(this.hibernateNamedNativeQueries);
	}
	
	// ********** AnnotationContainer implementation **********

	public String getContainerAnnotationName() {
		return this.getAnnotationName();
	}

	public org.eclipse.jdt.core.dom.Annotation getContainerJdtAnnotation(CompilationUnit astRoot) {
		return this.getJdtAnnotation(astRoot);
	}

	public String getElementName() {
		return Hibernate.NAMED_NATIVE_QUERIES__VALUE;
	}

	public String getNestableAnnotationName() {
		return HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME;
	}

	public ListIterator<HibernateNamedNativeQueryAnnotation> nestedAnnotations() {
		return new CloneListIterator<HibernateNamedNativeQueryAnnotation>(this.hibernateNamedNativeQueries);
	}

	public int nestedAnnotationsSize() {
		return this.hibernateNamedNativeQueries.size();
	}

	public HibernateNamedNativeQueryAnnotation addNestedAnnotationInternal() {
		HibernateNamedNativeQueryAnnotation namedQuery = this.buildHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.size());
		this.hibernateNamedNativeQueries.add(namedQuery);
		return namedQuery;
	}

	private HibernateNamedNativeQueryAnnotation buildHibernateNamedNativeQuery(int index) {
		return HibernateSourceNamedNativeQueryAnnotation.createNestedHibernateNamedNativeQuery(this, member, index, this.daa);
	}
	
	public void nestedAnnotationAdded(int index, HibernateNamedNativeQueryAnnotation nestedAnnotation) {
		this.fireItemAdded(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, nestedAnnotation);
	}

	public HibernateNamedNativeQueryAnnotation moveNestedAnnotationInternal(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.hibernateNamedNativeQueries, targetIndex, sourceIndex).get(targetIndex);
	}

	public void nestedAnnotationMoved(int targetIndex, int sourceIndex) {
		this.fireItemMoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, targetIndex, sourceIndex);
	}

	public HibernateNamedNativeQueryAnnotation removeNestedAnnotationInternal(int index) {
		return this.hibernateNamedNativeQueries.remove(index);
	}

	public void nestedAnnotationRemoved(int index, HibernateNamedNativeQueryAnnotation nestedAnnotation) {
		this.fireItemRemoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, nestedAnnotation);
	}


}

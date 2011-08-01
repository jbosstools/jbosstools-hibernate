/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.Vector;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterables.LiveCloneIterable;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.jpa.core.resource.java.NestableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateSourceNamedQueriesAnnotation extends SourceAnnotation<AnnotatedElement> implements
		HibernateNamedQueriesAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private final Vector<HibernateNamedQueryAnnotation> hibernateNamedQueries = new Vector<HibernateNamedQueryAnnotation>();


	public HibernateSourceNamedQueriesAnnotation(JavaResourceNode parent, AnnotatedElement member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		AnnotationContainerTools.initialize(this, astRoot);
	}

	public void synchronizeWith(CompilationUnit astRoot) {
		AnnotationContainerTools.synchronize(this, astRoot);
	}

	@Override
	public boolean isUnset() {
		return super.isUnset() &&
				this.hibernateNamedQueries.isEmpty();
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.hibernateNamedQueries);
	}

	// ********** AnnotationContainer implementation **********
	public String getElementName() {
		return Hibernate.NAMED_QUERIES__VALUE;
	}

	public String getNestedAnnotationName() {
		return HibernateNamedQueryAnnotation.ANNOTATION_NAME;
	}

	public Iterable<HibernateNamedQueryAnnotation> getNestedAnnotations() {
		return new LiveCloneIterable<HibernateNamedQueryAnnotation>(this.hibernateNamedQueries);
	}

	public int getNestedAnnotationsSize() {
		return this.hibernateNamedQueries.size();
	}

	public void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation) {
		this.nestStandAloneAnnotation(standAloneAnnotation, this.hibernateNamedQueries.size());
	}

	private void nestStandAloneAnnotation(NestableAnnotation standAloneAnnotation, int index) {
		standAloneAnnotation.convertToNested(this, this.daa, index);
	}

	public void addNestedAnnotation(int index, NestableAnnotation annotation) {
		this.hibernateNamedQueries.add(index, (HibernateNamedQueryAnnotation) annotation);
	}

	public void convertLastNestedAnnotationToStandAlone() {
		this.hibernateNamedQueries.remove(0).convertToStandAlone();
	}

	public HibernateNamedQueryAnnotation addNestedAnnotation() {
		return this.addNestedAnnotation(this.hibernateNamedQueries.size());
	}

	private HibernateNamedQueryAnnotation addNestedAnnotation(int index) {
		HibernateNamedQueryAnnotation namedQuery = this.buildHibernateNamedQuery(index);
		this.hibernateNamedQueries.add(namedQuery);
		return namedQuery;
	}

	public void syncAddNestedAnnotation(Annotation astAnnotation) {
		int index = this.hibernateNamedQueries.size();
		HibernateNamedQueryAnnotation namedQuery = this.addNestedAnnotation(index);
		namedQuery.initialize((CompilationUnit) astAnnotation.getRoot());
		this.fireItemAdded(HIBERNATE_NAMED_QUERIES_LIST, index, namedQuery);
	}

	private HibernateNamedQueryAnnotation buildHibernateNamedQuery(int index) {
		return HibernateSourceNamedQueryAnnotation.createNestedHibernateNamedQuery(this.parent, this.annotatedElement, index, this.daa);
	}

	public HibernateNamedQueryAnnotation moveNestedAnnotation(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.hibernateNamedQueries, targetIndex, sourceIndex).get(targetIndex);
	}

	public HibernateNamedQueryAnnotation removeNestedAnnotation(int index) {
		return this.hibernateNamedQueries.remove(index);
	}

	public void syncRemoveNestedAnnotations(int index) {
		this.removeItemsFromList(index, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}

}

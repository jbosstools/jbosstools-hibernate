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
import org.eclipse.jpt.core.internal.resource.java.source.AnnotationContainerTools;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;
import org.eclipse.jpt.utility.internal.CollectionTools;
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

	public void synchronizeWith(CompilationUnit astRoot) {
		AnnotationContainerTools.synchronize(this, astRoot);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.hibernateNamedNativeQueries);
	}
	
	// ********** AnnotationContainer implementation **********
	public String getElementName() {
		return Hibernate.NAMED_NATIVE_QUERIES__VALUE;
	}
	
	public String getNestedAnnotationName() {
		return HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME;
	}	

	public String getContainerAnnotationName() {
		return this.getAnnotationName();
	}
	
	public Iterable<HibernateNamedNativeQueryAnnotation> getNestedAnnotations() {
		return this.hibernateNamedNativeQueries;
	}
	
	public int getNestedAnnotationsSize() {
		return this.hibernateNamedNativeQueries.size();
	}
	
	public HibernateNamedNativeQueryAnnotation addNestedAnnotation() {
		return this.addNestedAnnotation(this.hibernateNamedNativeQueries.size());
	}
	
	private HibernateNamedNativeQueryAnnotation addNestedAnnotation(int index) {
		HibernateNamedNativeQueryAnnotation namedNativeQuery = this.buildHibernateNamedNativeQuery(index);
		this.hibernateNamedNativeQueries.add(namedNativeQuery);
		return namedNativeQuery;
	}
	
	public void syncAddNestedAnnotation(Annotation astAnnotation) {
		int index = this.hibernateNamedNativeQueries.size();
		HibernateNamedNativeQueryAnnotation namedNativeQuery = this.addNestedAnnotation(index);
		namedNativeQuery.initialize((CompilationUnit) astAnnotation.getRoot());
		this.fireItemAdded(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, namedNativeQuery);
	}
	
	private HibernateNamedNativeQueryAnnotation buildHibernateNamedNativeQuery(int index) {
		return HibernateSourceNamedNativeQueryAnnotation.createNestedHibernateNamedNativeQuery(this, member, index, this.daa);
	}

	public org.eclipse.jdt.core.dom.Annotation getContainerJdtAnnotation(CompilationUnit astRoot) {
		return this.getAstAnnotation(astRoot);
	}

	public HibernateNamedNativeQueryAnnotation moveNestedAnnotation(int targetIndex, int sourceIndex) {
		return CollectionTools.move(this.hibernateNamedNativeQueries, targetIndex, sourceIndex).get(targetIndex);
	}

	public HibernateNamedNativeQueryAnnotation removeNestedAnnotation(int index) {
		return this.hibernateNamedNativeQueries.remove(index);
	}

	public void syncRemoveNestedAnnotations(int index) {
		this.removeItemsFromList(index, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}

}

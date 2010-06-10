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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.context.java.JavaNamedNativeQuery;
import org.eclipse.jpt.core.context.java.JavaNamedQuery;
import org.eclipse.jpt.core.internal.jpa1.context.java.GenericJavaQueryContainer;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.java.NamedNativeQueryAnnotation;
import org.eclipse.jpt.core.resource.java.NamedQueryAnnotation;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaQueryContainerImpl extends GenericJavaQueryContainer
implements HibernateJavaQueryContainer{
	
	protected final List<HibernateNamedQuery> hibernateNamedQueries;

	protected final List<HibernateNamedNativeQuery> hibernateNamedNativeQueries;

	public HibernateJavaQueryContainerImpl(JavaJpaContextNode parent) {
		super(parent);
		this.hibernateNamedQueries = new ArrayList<HibernateNamedQuery>();
		this.hibernateNamedNativeQueries = new ArrayList<HibernateNamedNativeQuery>();
	}
	
	public HibernateJpaFactory getJpaFactory(){
		return (HibernateJpaFactory)super.getJpaFactory();
	}
	
	public void initialize(JavaResourcePersistentMember jrpm) {
		super.initialize(jrpm);
		initializeHibernateNamedQueries();
		initializeHibernateNamedNativeQueries();
	}
	
	public void update(JavaResourcePersistentMember jrpm) {
		super.update(jrpm);
		this.updateHibernateNamedQueries();
		this.updateHibernateNamedNativeQueries();
	}
	
	// *********** Named Queries

	protected void initializeHibernateNamedQueries() {
		for (Iterator<NestableAnnotation> stream = this.javaResourcePersistentMember.
					annotations(
						HibernateNamedQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedQueriesAnnotation.ANNOTATION_NAME); 
					stream.hasNext(); ) {
			this.hibernateNamedQueries.add(buildHibernateNamedQuery((NamedQueryAnnotation) stream.next()));
		}
	}
	
	protected void updateHibernateNamedQueries() {
		ListIterator<HibernateNamedQuery> queries = hibernateNamedQueries();
		Iterator<NestableAnnotation> resourceNamedQueries = 
				this.javaResourcePersistentMember.annotations(
						HibernateNamedQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedQuery namedQuery = queries.next();
			if (resourceNamedQueries.hasNext()) {
				namedQuery.update((NamedQueryAnnotation) resourceNamedQueries.next());
			}
			else {
				removeHibernateNamedQuery_(namedQuery);
			}
		}
		
		while (resourceNamedQueries.hasNext()) {
			addHibernateNamedQuery(buildHibernateNamedQuery((NamedQueryAnnotation) resourceNamedQueries.next()));
		}
	}	
	
	public HibernateNamedQuery addHibernateNamedQuery(int index) {
		HibernateNamedQuery namedQuery = getJpaFactory().buildHibernateJavaNamedQuery(this);
		this.hibernateNamedQueries.add(index, namedQuery);
		NamedQueryAnnotation namedQueryAnnotation = 
				(NamedQueryAnnotation) this.javaResourcePersistentMember.
					addAnnotation(
						index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		namedQuery.initialize(namedQueryAnnotation);
		fireItemAdded(HIBERNATE_NAMED_QUERIES_LIST, index, namedQuery);
		return namedQuery;
	}
	
	public ListIterator<HibernateNamedQuery> hibernateNamedQueries() {
		return new CloneListIterator<HibernateNamedQuery>(this.hibernateNamedQueries);
	}

	public int hibernateNamedQueriesSize() {
		return this.hibernateNamedQueries.size();
	}

	public void removeHibernateNamedQuery(int index) {
		JavaNamedQuery removedNamedQuery = this.hibernateNamedQueries.remove(index);
		this.javaResourcePersistentMember.removeAnnotation(
				index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_QUERIES_LIST, index, removedNamedQuery);
	}

	public void removeHibernateNamedQuery(HibernateNamedQuery namedQuery) {
		removeHibernateNamedQuery(this.hibernateNamedQueries.indexOf(namedQuery));
	}
	
	protected void removeHibernateNamedQuery_(HibernateNamedQuery namedQuery) {
		removeItemFromList(namedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}

	public void moveHibernateNamedQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentMember.moveAnnotation(
				targetIndex, sourceIndex, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_QUERIES_LIST, targetIndex, sourceIndex);
	}
	
	protected void addHibernateNamedQuery(int index, HibernateNamedQuery hibernateNamedQuery) {
		addItemToList(index, hibernateNamedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}
	
	protected void addHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		this.addHibernateNamedQuery(this.hibernateNamedQueries.size(), hibernateNamedQuery);
	}
	
	protected HibernateNamedQuery buildHibernateNamedQuery(NamedQueryAnnotation namedQueryResource) {
		HibernateNamedQuery namedQuery = getJpaFactory().buildHibernateJavaNamedQuery(this);
		namedQuery.initialize(namedQueryResource);
		return namedQuery;
	}


	// *********** Native Queries
	
	protected void initializeHibernateNamedNativeQueries() {
		for (Iterator<NestableAnnotation> stream = this.javaResourcePersistentMember.
					annotations(
						HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME); 
					stream.hasNext(); ) {
			this.hibernateNamedNativeQueries.add(buildHibernateNamedNativeQuery((NamedNativeQueryAnnotation) stream.next()));
		}
	}
	
	protected void updateHibernateNamedNativeQueries() {
		ListIterator<HibernateNamedNativeQuery> queries = hibernateNamedNativeQueries();
		Iterator<NestableAnnotation> resourceNamedNativeQueries = 
				this.javaResourcePersistentMember.annotations(
						HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedNativeQuery namedQuery = queries.next();
			if (resourceNamedNativeQueries.hasNext()) {
				namedQuery.update((NamedNativeQueryAnnotation) resourceNamedNativeQueries.next());
			}
			else {
				removeHibernateNamedNativeQuery_(namedQuery);
			}
		}
		
		while (resourceNamedNativeQueries.hasNext()) {
			addHibernateNamedNativeQuery(buildHibernateNamedNativeQuery((HibernateNamedNativeQueryAnnotation) resourceNamedNativeQueries.next()));
		}
	}
	
	protected HibernateNamedNativeQuery buildHibernateNamedNativeQuery(NamedNativeQueryAnnotation namedNativeQueryResource) {
		HibernateNamedNativeQuery namedNativeQuery = getJpaFactory().buildHibernateJavaNamedNativeQuery(this);
		namedNativeQuery.initialize(namedNativeQueryResource);
		return namedNativeQuery;
	}
	
	public HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQuery namedNativeQuery = getJpaFactory().buildHibernateJavaNamedNativeQuery(this);
		this.hibernateNamedNativeQueries.add(index, namedNativeQuery);
		NamedNativeQueryAnnotation namedNativeQueryAnnotation = 
				(NamedNativeQueryAnnotation) this.javaResourcePersistentMember.
					addAnnotation(
						index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, 
						HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		namedNativeQuery.initialize(namedNativeQueryAnnotation);		
		fireItemAdded(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, namedNativeQuery);
		return namedNativeQuery;
	}
	
	protected void addHibernateNamedNativeQuery(int index, HibernateNamedNativeQuery namedNativeQuery) {
		addItemToList(index, namedNativeQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}
	
	protected void addHibernateNamedNativeQuery(HibernateNamedNativeQuery namedNativeQuery) {
		this.addHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.size(), namedNativeQuery);
	}

	public ListIterator<HibernateNamedNativeQuery> hibernateNamedNativeQueries() {
		return new CloneListIterator<HibernateNamedNativeQuery>(this.hibernateNamedNativeQueries);
	}

	public int hibernateNamedNativeQueriesSize() {
		return this.hibernateNamedNativeQueries.size();
	}

	public void removeHibernateNamedNativeQuery(int index) {
		JavaNamedNativeQuery removedNamedQuery = this.hibernateNamedNativeQueries.remove(index);
		this.javaResourcePersistentMember.removeAnnotation(
				index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, removedNamedQuery);
	}

	public void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery namedQuery) {
		removeHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.indexOf(namedQuery));
	}
	
	protected void removeHibernateNamedNativeQuery_(HibernateNamedNativeQuery namedQuery) {
		removeItemFromList(namedQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}

	public void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedNativeQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentMember.moveAnnotation(
				targetIndex, sourceIndex, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, targetIndex, sourceIndex);
	}

}

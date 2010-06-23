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

import org.eclipse.jpt.core.context.java.JavaGenerator;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.jpa1.context.java.GenericJavaGeneratorContainer;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorsAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaGeneratorContainerImpl extends
		GenericJavaGeneratorContainer implements
		HibernateJavaGeneratorContainer {
	
	protected final List<JavaGenericGenerator> genericGenerators;

	public HibernateJavaGeneratorContainerImpl(JavaJpaContextNode parent) {
		super(parent);
		this.genericGenerators = new ArrayList<JavaGenericGenerator>();
	}
	
	public HibernateAbstractJpaFactory getJpaFactory(){
		return (HibernateAbstractJpaFactory)super.getJpaFactory();
	}
	
	@Override
	public void initialize(JavaResourcePersistentMember jrpm) {
		super.initialize(jrpm);
		this.initializeGenericGenerators();
	}
	
	@Override
	public void update(JavaResourcePersistentMember jrpm) {
		super.update(jrpm);
		this.updateGenericGenerators();
	}

	public JavaGenericGenerator addGenericGenerator(int index) {
		JavaGenericGenerator newGenericGenerator = getJpaFactory().buildJavaGenericGenerator(this);
		this.genericGenerators.add(index, newGenericGenerator);
		GenericGeneratorAnnotation genericGeneratorAnnotation = (GenericGeneratorAnnotation)this.javaResourcePersistentMember
			.addAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		newGenericGenerator.initialize(genericGeneratorAnnotation);
		fireItemAdded(GENERIC_GENERATORS_LIST, index, newGenericGenerator);
		return newGenericGenerator;
	}
	
	protected void addGenericGenerator(JavaGenericGenerator genericGenerator) {
		this.addGenericGenerator(genericGeneratorsSize(), genericGenerator);
	}
	
	protected void addGenericGenerator(int index, JavaGenericGenerator genericGenerator) {
		addItemToList(index, genericGenerator, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}

	public ListIterator<JavaGenericGenerator> genericGenerators() {
		return new CloneListIterator<JavaGenericGenerator>(genericGenerators);
	}

	public int genericGeneratorsSize() {
		return this.genericGenerators.size();
	}

	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.genericGenerators, targetIndex, sourceIndex);
		this.javaResourcePersistentMember.moveAnnotation(targetIndex, sourceIndex, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		fireItemMoved(GENERIC_GENERATORS_LIST, targetIndex, sourceIndex);		
	}

	public void removeGenericGenerator(int index) {
		JavaGenericGenerator removedGenericGenerator = this.genericGenerators.remove(index);
		this.javaResourcePersistentMember.removeAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		fireItemRemoved(GENERIC_GENERATORS_LIST, index, removedGenericGenerator);
	}

	public void removeGenericGenerator(GenericGenerator generator) {
		removeGenericGenerator(this.genericGenerators.indexOf(generator));		
	}

	protected void removeGenericGenerator_(JavaGenericGenerator generator) {
		removeItemFromList(generator, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}

	protected void initializeGenericGenerators() {
		for (Iterator<NestableAnnotation> stream = this.javaResourcePersistentMember.annotations(
				GenericGeneratorAnnotation.ANNOTATION_NAME,
				GenericGeneratorsAnnotation.ANNOTATION_NAME);
		stream.hasNext(); ) {
			this.genericGenerators.add(buildGenericGenerator((GenericGeneratorAnnotation) stream.next()));
		}
	}
	
	protected JavaGenericGenerator buildGenericGenerator(GenericGeneratorAnnotation genericGeneratorResource) {
		JavaGenericGenerator generator = getJpaFactory().buildJavaGenericGenerator(this);
		generator.initialize(genericGeneratorResource);
		return generator;
	}
	
	@Override
	protected void addGeneratorsTo(ArrayList<JavaGenerator> generators) {
		super.addGeneratorsTo(generators);
		for (JavaGenericGenerator genericGenerator : genericGenerators) {
			generators.add(genericGenerator);
		}
	}
	
	protected void updateGenericGenerators() {
		ListIterator<JavaGenericGenerator> genericGenerators = genericGenerators();
		Iterator<NestableAnnotation> resourceGenericGenerators =
			this.javaResourcePersistentMember.annotations(
					GenericGeneratorAnnotation.ANNOTATION_NAME,
					GenericGeneratorsAnnotation.ANNOTATION_NAME);

		while (genericGenerators.hasNext()) {
			JavaGenericGenerator genericGenerator = genericGenerators.next();
			if (resourceGenericGenerators.hasNext()) {
				genericGenerator.update((GenericGeneratorAnnotation) resourceGenericGenerators.next());
			}
			else {
				removeGenericGenerator_(genericGenerator);
			}
		}

		while (resourceGenericGenerators.hasNext()) {
			addGenericGenerator(buildGenericGenerator((GenericGeneratorAnnotation) resourceGenericGenerators.next()));
		}
	}


}

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

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.Iterator;

import org.eclipse.jpt.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaBasicMapping;
import org.eclipse.jpt.core.resource.java.JPA;
import org.eclipse.jpt.utility.internal.iterators.ArrayIterator;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GeneratedAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaBasicMappingImpl extends AbstractJavaBasicMapping
implements Generated {
	
	protected GenerationTime specifiedGenerationTime;

	public HibernateJavaBasicMappingImpl(JavaPersistentAttribute parent) {
		super(parent);
	}
	
	public Iterator<String> supportingAnnotationNames() {
		return new ArrayIterator<String>(
			JPA.COLUMN,
			JPA.LOB,
			JPA.TEMPORAL,
			JPA.ENUMERATED,
			Hibernate.GENERATED);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.specifiedGenerationTime = this.getResourceGenerationTime();
	}
	
	public GeneratedAnnotation getResourceGenerated() {
		return (GeneratedAnnotation) getResourcePersistentAttribute().getSupportingAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	public GeneratedAnnotation addResourceGenerated() {
		return (GeneratedAnnotation) getResourcePersistentAttribute().addSupportingAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	public void removeResourceGenerated() {
		getResourcePersistentAttribute().removeSupportingAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}
	
	protected GenerationTime getResourceGenerationTime(){
		GeneratedAnnotation geneatedAnnotation = getResourceGenerated();
		return geneatedAnnotation == null ? null : geneatedAnnotation.getValue();
	}
	
	public GenerationTime getGenerationTime() {
		return this.specifiedGenerationTime;
	}
	
	public void setGenerationTime(GenerationTime newValue) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newValue;
		if (newValue != null){
			GeneratedAnnotation annotation = getResourceGenerated() != null
						? getResourceGenerated()
						: addResourceGenerated();
			annotation.setValue(newValue);
		} else {
			removeResourceGenerated();
		}
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newValue);
	}

	public void setGenerationTime_(GenerationTime newGenerationTime) {
		GenerationTime oldValue = this.specifiedGenerationTime;
		this.specifiedGenerationTime = newGenerationTime;
		firePropertyChanged(Generated.GENERATION_TIME_PROPERTY, oldValue, newGenerationTime);
	}
	
	@Override
	protected void update() {
		super.update();
		this.setGenerationTime_(this.getResourceGenerationTime());
	}
	
}

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.context.BaseJoinColumn;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.Table;
import org.eclipse.jpt.core.context.TypeMapping;
import org.eclipse.jpt.core.context.java.JavaBaseJoinColumn;
import org.eclipse.jpt.core.context.java.JavaGenerator;
import org.eclipse.jpt.core.context.java.JavaPersistentType;
import org.eclipse.jpt.core.context.java.JavaQuery;
import org.eclipse.jpt.core.context.java.JavaBaseJoinColumn.Owner;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaEntity;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.utility.Filter;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.iterators.ArrayIterator;
import org.eclipse.jpt.utility.internal.iterators.CloneListIterator;
import org.eclipse.jpt.utility.internal.iterators.CompositeIterator;
import org.eclipse.jpt.utility.internal.iterators.TransformationIterator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.context.ForeignKey;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.DiscriminatorFormulaAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorsAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueriesAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaEntityImpl extends AbstractJavaEntity 
implements HibernateJavaEntity {
	
	protected JavaDiscriminatorFormula discriminatorFormula;

	protected final List<JavaGenericGenerator> genericGenerators;
	
	protected final List<HibernateNamedQuery> hibernateNamedQueries;
	
	protected final List<HibernateNamedNativeQuery> hibernateNamedNativeQueries;
	
	protected ForeignKey foreignKey;
	
	public HibernateJavaEntityImpl(JavaPersistentType parent) {
		super(parent);
		this.genericGenerators = new ArrayList<JavaGenericGenerator>();
		this.hibernateNamedQueries = new ArrayList<HibernateNamedQuery>();
		this.hibernateNamedNativeQueries = new ArrayList<HibernateNamedNativeQuery>();
	}
	
	@Override
	public void initialize(JavaResourcePersistentType resourcePersistentType) {
		super.initialize(resourcePersistentType);
		this.initializeDiscriminatorFormula();
		this.initializeGenericGenerators();
		this.initializeHibernateNamedQueries();
		this.initializeHibernateNamedNativeQueries();
		this.initializeForeignKey();
	}
	
	@Override
	public void update(JavaResourcePersistentType resourcePersistentType) {
		super.update(resourcePersistentType);
		this.updateDiscriminatorFormula();
		this.updateGenericGenerators();
		this.updateHibernateNamedQueries();
		this.updateHibernateNamedNativeQueries();
		this.updateForeignKey();
	}	
	
	protected HibernateJpaFactory getJpaFactory() {
		return (HibernateJpaFactory) this.getJpaPlatform().getJpaFactory();
	}
	
	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<JavaQuery> queries() {
		return new CompositeIterator<JavaQuery>(
				super.queries(),
				this.hibernateNamedQueries(),
				this.hibernateNamedNativeQueries());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<String> correspondingAnnotationNames() {
		return new CompositeIterator<String>(
				new ArrayIterator<String>(
					Hibernate.GENERIC_GENERATOR,
					Hibernate.GENERIC_GENERATORS, 
					Hibernate.NAMED_QUERY,
					Hibernate.NAMED_QUERIES,
					Hibernate.NAMED_NATIVE_QUERY,
					Hibernate.NAMED_NATIVE_QUERIES,
					Hibernate.DISCRIMINATOR_FORMULA,
					Hibernate.FOREIGN_KEY),
				super.correspondingAnnotationNames());
	}
	
	public HibernateJavaTable getTable() {
		return (HibernateJavaTable) super.getTable();
	}
	
	// ********************* DiscriminatorFormula **************	
	public JavaDiscriminatorFormula getDiscriminatorFormula() {
		return this.discriminatorFormula;
	}
	
	protected void setDiscriminatorFormula(JavaDiscriminatorFormula newDiscriminatorFormula) {
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = newDiscriminatorFormula;
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula, newDiscriminatorFormula);
	}
	
	public JavaDiscriminatorFormula addDiscriminatorFormula() {
		if (getDiscriminatorFormula() != null) {
			throw new IllegalStateException("discriminatorFormula already exists"); //$NON-NLS-1$
		}
		this.discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this);
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = (DiscriminatorFormulaAnnotation) this.javaResourcePersistentType.addSupportingAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		this.discriminatorFormula.initialize(discriminatorFormulaResource);
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, null, this.discriminatorFormula);
		return this.discriminatorFormula;
	}
	
	public void removeDiscriminatorFormula() {
		if (getDiscriminatorFormula() == null) {
			throw new IllegalStateException("discriminatorFormula does not exist, cannot be removed"); //$NON-NLS-1$
		}
		JavaDiscriminatorFormula oldDiscriminatorFormula = this.discriminatorFormula;
		this.discriminatorFormula = null;
		this.javaResourcePersistentType.removeSupportingAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
		firePropertyChanged(DISCRIMINATOR_FORMULA_PROPERTY, oldDiscriminatorFormula,null);
	}
	
	protected void initializeDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = getDiscriminatorFormulaResource();
		if (discriminatorFormulaResource != null) {
			this.discriminatorFormula = buildDiscriminatorFormula(discriminatorFormulaResource);
		}
	}

	protected void updateDiscriminatorFormula() {
		DiscriminatorFormulaAnnotation discriminatorFormulaResource = getDiscriminatorFormulaResource();
		if (discriminatorFormulaResource == null) {
			if (getDiscriminatorFormula() != null) {
				setDiscriminatorFormula(null);
			}
		}
		else {
			if (getDiscriminatorFormula() == null) {
				setDiscriminatorFormula(buildDiscriminatorFormula(discriminatorFormulaResource));
			}
			else {
				getDiscriminatorFormula().update(discriminatorFormulaResource);
			}
		}
	}

	public DiscriminatorFormulaAnnotation getDiscriminatorFormulaResource() {
		return (DiscriminatorFormulaAnnotation) this.javaResourcePersistentType.getSupportingAnnotation(DiscriminatorFormulaAnnotation.ANNOTATION_NAME);
	}
	
	protected JavaDiscriminatorFormula buildDiscriminatorFormula(DiscriminatorFormulaAnnotation discriminatorFormulaResource) {
		JavaDiscriminatorFormula discriminatorFormula = getJpaFactory().buildJavaDiscriminatorFormula(this);
		discriminatorFormula.initialize(discriminatorFormulaResource);
		return discriminatorFormula;
	}
	// ********************* GenericGenerators **************

	public GenericGenerator addGenericGenerator(int index) {
		JavaGenericGenerator newGenericGenerator = getJpaFactory().buildJavaGenericGenerator(this);
		this.genericGenerators.add(newGenericGenerator);
		GenericGeneratorAnnotation genericGeneratorAnnotation = (GenericGeneratorAnnotation)this.javaResourcePersistentType
			.addSupportingAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
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

	@SuppressWarnings("unchecked")
	public ListIterator<JavaGenericGenerator> genericGenerators() {
		return new CloneListIterator<JavaGenericGenerator>(genericGenerators);
	}

	public int genericGeneratorsSize() {
		return this.genericGenerators.size();
	}

	public void moveGenericGenerator(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.genericGenerators, targetIndex, sourceIndex);
		this.javaResourcePersistentType.moveSupportingAnnotation(targetIndex, sourceIndex, GenericGeneratorAnnotation.ANNOTATION_NAME);
		fireItemMoved(GENERIC_GENERATORS_LIST, targetIndex, sourceIndex);		
	}

	public void removeGenericGenerator(int index) {
		JavaGenericGenerator removedGenericGenerator = this.genericGenerators.remove(index);
		this.javaResourcePersistentType.removeSupportingAnnotation(index, GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);
		fireItemRemoved(GENERIC_GENERATORS_LIST, index, removedGenericGenerator);
	}

	public void removeGenericGenerator(GenericGenerator generator) {
		removeGenericGenerator(this.genericGenerators.indexOf(generator));		
	}

	protected void removeGenericGenerator_(JavaGenericGenerator generator) {
		removeItemFromList(generator, this.genericGenerators, GENERIC_GENERATORS_LIST);
	}

	protected void initializeGenericGenerators() {
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME); stream.hasNext(); ) {
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
		ListIterator<NestableAnnotation> resourceGenericGenerators = this.javaResourcePersistentType.supportingAnnotations(GenericGeneratorAnnotation.ANNOTATION_NAME, GenericGeneratorsAnnotation.ANNOTATION_NAME);

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
	// ********************* NamedQuery **************
	public ListIterator<HibernateNamedQuery> hibernateNamedQueries() {
		return new CloneListIterator<HibernateNamedQuery>(this.hibernateNamedQueries);
	}
	
	public int hibernateNamedQueriesSize() {
		return this.hibernateNamedQueries.size();
	}
	
	protected void initializeHibernateNamedQueries() {
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME); stream.hasNext(); ) {
			this.hibernateNamedQueries.add(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) stream.next()));
		}
	}
	
	protected HibernateNamedQuery buildHibernateNamedQuery(HibernateNamedQueryAnnotation namedQueryResource) {
		HibernateNamedQuery hibernateNamedQuery = getJpaFactory().buildHibernateNamedQuery(this);
		hibernateNamedQuery.initialize(namedQueryResource);
		return hibernateNamedQuery;
	}
	
	protected void updateHibernateNamedQueries() {
		ListIterator<HibernateNamedQuery> queries = hibernateNamedQueries();
		ListIterator<NestableAnnotation> resourceNamedQueries = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedQuery hibernateNamedQuery = queries.next();
			if (resourceNamedQueries.hasNext()) {
				hibernateNamedQuery.update((HibernateNamedQueryAnnotation) resourceNamedQueries.next());
			}
			else {
				removeHibernateNamedQuery_(hibernateNamedQuery);
			}
		}
		
		while (resourceNamedQueries.hasNext()) {
			addHibernateNamedQuery(buildHibernateNamedQuery((HibernateNamedQueryAnnotation) resourceNamedQueries.next()));
		}
	}
	
	public HibernateNamedQuery addHibernateNamedQuery(int index) {
		HibernateNamedQuery hibernateNamedQuery = getJpaFactory().buildHibernateNamedQuery(this);
		this.hibernateNamedQueries.add(index, hibernateNamedQuery);
		HibernateNamedQueryAnnotation hibernateNamedQueryAnnotation = (HibernateNamedQueryAnnotation) this.javaResourcePersistentType
			.addSupportingAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		hibernateNamedQuery.initialize(hibernateNamedQueryAnnotation);
		fireItemAdded(HIBERNATE_NAMED_QUERIES_LIST, index, hibernateNamedQuery);
		return hibernateNamedQuery;
	}
	
	protected void addHibernateNamedQuery(int index, HibernateNamedQuery hibernateNamedQuery) {
		addItemToList(index, hibernateNamedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}
	
	protected void addHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		this.addHibernateNamedQuery(this.hibernateNamedQueries.size(), hibernateNamedQuery);
	}
	
	public void removeHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		removeHibernateNamedQuery(this.hibernateNamedQueries.indexOf(hibernateNamedQuery));
	}
	
	public void removeHibernateNamedQuery(int index) {
		HibernateNamedQuery removedHibernateNamedQuery = this.hibernateNamedQueries.remove(index);
		this.javaResourcePersistentType.removeSupportingAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_QUERIES_LIST, index, removedHibernateNamedQuery);
	}	
	
	protected void removeHibernateNamedQuery_(HibernateNamedQuery hibernateNamedQuery) {
		removeItemFromList(hibernateNamedQuery, this.hibernateNamedQueries, HIBERNATE_NAMED_QUERIES_LIST);
	}
	
	public void moveHibernateNamedQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentType.moveSupportingAnnotation(targetIndex, sourceIndex, HibernateNamedQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_QUERIES_LIST, targetIndex, sourceIndex);		
	}
	
	// ********************* NamedNativeQuery **************
	public ListIterator<HibernateNamedNativeQuery> hibernateNamedNativeQueries() {
		return new CloneListIterator<HibernateNamedNativeQuery>(this.hibernateNamedNativeQueries);
	}
	
	public int hibernateNamedNativeQueriesSize() {
		return this.hibernateNamedNativeQueries.size();
	}
	
	protected void initializeHibernateNamedNativeQueries() {
		for (ListIterator<NestableAnnotation> stream = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME); stream.hasNext(); ) {
			this.hibernateNamedNativeQueries.add(buildHibernateNamedNativeQuery((HibernateNamedNativeQueryAnnotation) stream.next()));
		}
	}
	
	protected HibernateNamedNativeQuery buildHibernateNamedNativeQuery(HibernateNamedNativeQueryAnnotation namedQueryResource) {
		HibernateNamedNativeQuery hibernateNamedNativeQuery = getJpaFactory().buildHibernateNamedNativeQuery(this);
		hibernateNamedNativeQuery.initialize(namedQueryResource);
		return hibernateNamedNativeQuery;
	}
	
	protected void updateHibernateNamedNativeQueries() {
		ListIterator<HibernateNamedNativeQuery> queries = hibernateNamedNativeQueries();
		ListIterator<NestableAnnotation> resourceNamedNativeQueries = this.javaResourcePersistentType.supportingAnnotations(HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		
		while (queries.hasNext()) {
			HibernateNamedNativeQuery hibernateNamedNativeQuery = queries.next();
			if (resourceNamedNativeQueries.hasNext()) {
				hibernateNamedNativeQuery.update((HibernateNamedNativeQueryAnnotation) resourceNamedNativeQueries.next());
			}
			else {
				removeHibernateNamedNativeQuery_(hibernateNamedNativeQuery);
			}
		}
		
		while (resourceNamedNativeQueries.hasNext()) {
			Object test = resourceNamedNativeQueries.next();
			if (!(test instanceof HibernateNamedNativeQueryAnnotation)){
				System.out.println("error!");//$NON-NLS-1$
			}
			addHibernateNamedNativeQuery(buildHibernateNamedNativeQuery((HibernateNamedNativeQueryAnnotation) test));
		}
	}
	
	public HibernateNamedNativeQuery addHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQuery hibernateNamedNativeQuery = getJpaFactory().buildHibernateNamedNativeQuery(this);
		this.hibernateNamedNativeQueries.add(index, hibernateNamedNativeQuery);
		HibernateNamedNativeQueryAnnotation hibernateNamedNativeQueryAnnotation = (HibernateNamedNativeQueryAnnotation) this.javaResourcePersistentType
			.addSupportingAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		hibernateNamedNativeQuery.initialize(hibernateNamedNativeQueryAnnotation);
		fireItemAdded(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, hibernateNamedNativeQuery);
		return hibernateNamedNativeQuery;
	}
	
	protected void addHibernateNamedNativeQuery(int index, HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		addItemToList(index, hibernateNamedNativeQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}
	
	protected void addHibernateNamedNativeQuery(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		this.addHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.size(), hibernateNamedNativeQuery);
	}
	
	public void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		removeHibernateNamedNativeQuery(this.hibernateNamedNativeQueries.indexOf(hibernateNamedNativeQuery));
	}
	
	public void removeHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQuery removedHibernateNamedNativeQuery = this.hibernateNamedNativeQueries.remove(index);
		this.javaResourcePersistentType.removeSupportingAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemRemoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, index, removedHibernateNamedNativeQuery);
	}	
	
	protected void removeHibernateNamedNativeQuery_(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		removeItemFromList(hibernateNamedNativeQuery, this.hibernateNamedNativeQueries, HIBERNATE_NAMED_NATIVE_QUERIES_LIST);
	}
	
	public void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex) {
		CollectionTools.move(this.hibernateNamedNativeQueries, targetIndex, sourceIndex);
		this.javaResourcePersistentType.moveSupportingAnnotation(targetIndex, sourceIndex, HibernateNamedNativeQueriesAnnotation.ANNOTATION_NAME);
		fireItemMoved(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, targetIndex, sourceIndex);		
	}
	
	// ********************* foreignKey **************
	
	protected void initializeForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource != null) {
			this.foreignKey = buildForeignKey(foreignKeyResource);
		}
	}
	
	protected void updateForeignKey() {
		ForeignKeyAnnotation foreignKeyResource = getResourceForeignKey();
		if (foreignKeyResource == null) {
			if (getForeignKey() != null) {
				setForeignKey(null);
			}
		}
		else {
			if (getForeignKey() == null) {
				setForeignKey(buildForeignKey(foreignKeyResource));
			}
			else {
				getForeignKey().update(foreignKeyResource);
			}
		}
	}
	
	public ForeignKey addForeignKey() {
		if (getForeignKey() != null) {
			throw new IllegalStateException("foreignKey already exists"); //$NON-NLS-1$
		}
		this.foreignKey = getJpaFactory().buildForeignKey(this);
		ForeignKeyAnnotation foreignKeyResource = (ForeignKeyAnnotation) javaResourcePersistentType.addSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		this.foreignKey.initialize(foreignKeyResource);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, null, this.foreignKey);
		return this.foreignKey;
	}

	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}
	
	protected void setForeignKey(ForeignKey newForeignKey) {
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = newForeignKey;
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, newForeignKey);
	}

	public void removeForeignKey() {
		if (getForeignKey() == null) {
			throw new IllegalStateException("foreignKey does not exist, cannot be removed"); //$NON-NLS-1$
		}
		ForeignKey oldForeignKey = this.foreignKey;
		this.foreignKey = null;
		this.javaResourcePersistentType.removeSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
		firePropertyChanged(FOREIGN_KEY_PROPERTY, oldForeignKey, null);
	}
	
	protected ForeignKey buildForeignKey(ForeignKeyAnnotation foreignKeyResource) {
		ForeignKey foreignKey = getJpaFactory().buildForeignKey(this);
		foreignKey.initialize(foreignKeyResource);
		return foreignKey;
	}
	
	protected ForeignKeyAnnotation getResourceForeignKey() {
		return (ForeignKeyAnnotation) this.javaResourcePersistentType.getSupportingAnnotation(ForeignKeyAnnotation.ANNOTATION_NAME);
	}
	
	public org.eclipse.jpt.db.Table getForeignKeyDbTable() {
		return getPrimaryDbTable();
	}

	// ************************* validation ***********************
	@Override
	public void validate(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		validateGenericGenerator(messages, reporter, astRoot);
		this.validateForeignKey(messages, astRoot);
	}
	
	protected void validateGenericGenerator(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		for (JavaGenericGenerator genericGenerator : genericGenerators) {
			genericGenerator.validate(messages, reporter, astRoot);
		}	
	}
	
	protected void validateForeignKey(List<IMessage> messages, CompilationUnit astRoot) {
		org.eclipse.jpt.db.Table table = getForeignKeyDbTable();
		if (!shouldValidateAgainstDatabase() || foreignKey == null || table == null ){
			return;
		}		
		Iterator<org.eclipse.jpt.db.ForeignKey> fks = table.foreignKeys();
		while (fks.hasNext()) {
			org.eclipse.jpt.db.ForeignKey fk = (org.eclipse.jpt.db.ForeignKey) fks.next();
			if (foreignKey.getName().equals(fk.getIdentifier())){
				return;
			}
		}
		TextRange textRange = this.getResourceForeignKey().getNameTextRange(astRoot);
		IMessage message = new LocalMessage(IMessage.HIGH_SEVERITY, 
				Messages.UNRESOLVED_FOREIGN_KEY_NAME, new String[] {foreignKey.getName(), getPrimaryTableName()},
				this.foreignKey);
		message.setLineNo(textRange.getLineNumber());
		message.setOffset(textRange.getOffset());
		message.setLength(textRange.getLength());
		messages.add(message);		
	}
	
	@Override
	public Iterator<String> javaCompletionProposals(int pos, Filter<String> filter,
			CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		for (JavaGenericGenerator genericGenerator : genericGenerators) {
			result = genericGenerator.javaCompletionProposals(pos, filter, astRoot);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	/*protected String getResourceDefaultName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.classToTableName(javaResourcePersistentType.getName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return javaResourcePersistentType.getName();
	}*/
	
	@Override
	protected Owner createPrimaryKeyJoinColumnOwner() {
		return new HibernatePrimaryKeyJoinColumnOwner();
	}
	
	// ********** pk join column owner **********

	class HibernatePrimaryKeyJoinColumnOwner implements JavaBaseJoinColumn.Owner
	{
		public TextRange getValidationTextRange(CompilationUnit astRoot) {
			return HibernateJavaEntityImpl.this.getValidationTextRange(astRoot);
		}

		public TypeMapping getTypeMapping() {
			return HibernateJavaEntityImpl.this;
		}

		public org.eclipse.jpt.db.Table getDbTable(String tableName) {
			return HibernateJavaEntityImpl.this.getDbTable(tableName);
		}

		public org.eclipse.jpt.db.Table getReferencedColumnDbTable() {
			Entity parentEntity = HibernateJavaEntityImpl.this.getParentEntity();
			return (parentEntity == null) ? null : parentEntity.getPrimaryDbTable();
		}

		public int joinColumnsSize() {
			return HibernateJavaEntityImpl.this.primaryKeyJoinColumnsSize();
		}
		
		public boolean isVirtual(BaseJoinColumn joinColumn) {
			return HibernateJavaEntityImpl.this.defaultPrimaryKeyJoinColumn == joinColumn;
		}		
		
		public String getDefaultColumnName() {
			if (joinColumnsSize() != 1) {
				return null;
			}
			
			Entity parentEntity = HibernateJavaEntityImpl.this.getParentEntity();
			/*HibernateJpaProject hibernateJpaProject = HibernateJavaEntityImpl.this.getJpaProject();
			NamingStrategy ns = hibernateJpaProject.getNamingStrategy();
			if (hibernateJpaProject.isNamingStrategyEnabled() && ns != null) {
				try {
					String name = ns.joinKeyColumnName(parentEntity.getPrimaryKeyColumnName(),
						parentEntity.getPrimaryTableName());
					if (parentEntity.getPrimaryDbTable() != null){
						return parentEntity.getPrimaryDbTable().getDatabase().convertNameToIdentifier(name);
					}
					return name ;
				} catch (Exception e) {
					Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
							Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
					HibernateJptPlugin.logException(m.getText(), e);
				}
			}*/
			return parentEntity.getPrimaryKeyColumnName();
		}
	}

	@Override
	public String getPrimaryTableName() {
		return this.getTable().getDBTableName();
	}
	
	@Override
	public String getDefaultTableName() {
		return super.getDefaultTableName();
	}
	
	/**
	 * Convert Table to it's DB name.
	 */
	protected Iterator<String> tableNames(Iterator<Table> tables) {
		return new TransformationIterator<Table, String>(tables) {
			@Override
			protected String transform(Table t) {
				if (t instanceof HibernateTable) {
					return ((HibernateTable)t).getDBTableName();					
				} else {
					return t.getName();//What is this???
				}				
			}
		};
	}
	
}



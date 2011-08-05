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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.Filter;
import org.eclipse.jpt.jpa.core.JpaFile;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaStructureNodes;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.resource.java.source.SourceNode;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.jpa.core.resource.java.JavaResourcePackage;
import org.eclipse.jst.j2ee.model.internal.validation.ValidationCancelledException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePackageInfoImpl extends AbstractJavaJpaContextNode implements HibernatePackageInfo {
	
	//FIXME may be need  to create "Mapping" class??
	protected final HibernateJavaTypeDefContainer typeDefContainer;
	protected final JavaGeneratorContainer generatorContainer;
	protected final JavaQueryContainer queryContainer;

	private JavaResourcePackage resourcePackage;
	protected String name;
	
	/**
	 * @param parent
	 */
	public HibernatePackageInfoImpl(PersistentType.Owner owner, JavaResourcePackage resourcePackage) {
		super(owner);
		this.resourcePackage = resourcePackage;
		this.name = resourcePackage.getName();
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(this);
		this.generatorContainer = this.buildGeneratorContainer();
		this.queryContainer = this.buildQueryContainer();
	}
	
	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) this.getJpaPlatform().getJpaFactory();
	}
	
	public HibernateJavaTypeDefContainer getTypeDefContainer() {
		return this.typeDefContainer;
	}
	
	@Override
	public IResource getResource() {
		return resourcePackage.getFile();
	}

	// ********** synchronize/update **********

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setName(this.resourcePackage.getName());
		this.typeDefContainer.initialize(this.getResourcePackage());
		this.generatorContainer.synchronizeWithResourceModel();
		this.queryContainer.synchronizeWithResourceModel();
	}

	@Override
	public void update() {
		super.update();
		this.typeDefContainer.update(this.getResourcePackage());
		this.generatorContainer.update();
		this.queryContainer.update();
	}

	// ********** name **********

	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}
	
	public TextRange getSelectionTextRange() {
		return this.getSelectionTextRange(this.buildASTRoot());
	}
	
	protected TextRange getSelectionTextRange(CompilationUnit astRoot) {
		return this.resourcePackage.getNameTextRange(astRoot);
	}

	public TextRange getValidationTextRange(CompilationUnit astRoot) {
		return this.getSelectionTextRange(astRoot);
	}

	public TextRange getValidationTextRange() {
		return this.getSelectionTextRange();
	}
	
	// ********** generator container **********

	public JavaGeneratorContainer getGeneratorContainer() {
		return this.generatorContainer;
	}

	protected JavaGeneratorContainer buildGeneratorContainer() {
		return this.getJpaFactory().buildJavaGeneratorContainer(this, this);
	}

	// ********** query container **********

	public JavaQueryContainer getQueryContainer() {
		return this.queryContainer;
	}

	protected JavaQueryContainer buildQueryContainer() {
		return this.getJpaFactory().buildJavaQueryContainer(this, this);
	}
	
	protected CompilationUnit buildASTRoot() {
		return this.resourcePackage.getJavaResourceCompilationUnit().buildASTRoot();
	}

	// it would be nice if the we passed in an astRoot here, but then we
	// would need to pass it to the XML structure nodes too...
	public JpaStructureNode getStructureNode(int offset) {
		CompilationUnit astRoot = this.buildASTRoot();

		if (this.contains(offset, astRoot)) {
			return this;
		}
		return null;
	}
	
	protected boolean contains(int offset, CompilationUnit astRoot) {
		TextRange fullTextRange = this.resourcePackage.getTextRange(astRoot);
		// 'fullTextRange' will be null if the type no longer exists in the java;
		// the context model can be out of synch with the resource model
		// when a selection event occurs before the context model has a
		// chance to synch with the resource model via the update thread
		return (fullTextRange == null) ? false : fullTextRange.includes(offset);
	}


	public String getId() {
		//FIXME check this is correct
		return JavaStructureNodes.COMPILATION_UNIT_ID;
	}

	public void dispose() {
		this.unregisterRootStructureNode();
	}
	
	protected void unregisterRootStructureNode() {
		JpaFile jpaFile = this.getJpaFile();
		// the JPA file can be null if the .java file was deleted
		// or the resource type is "external"
		if (jpaFile != null) {
			jpaFile.removeRootStructureNode(this.name, this);
		}
	}
	
	protected JpaFile getJpaFile() {
		return this.getJpaFile(this.resourcePackage.getFile());
	}

	@Override
	public JavaResourcePackage getResourcePackage() {
		return this.resourcePackage;
	}
	
	@Override
	public JavaResourceAnnotatedElement getResourceAnnotatedElement() {
		return this.resourcePackage;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaPackageInfo#validate(java.util.List, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		if (reporter.isCancelled()) {
			throw new ValidationCancelledException();
		}
		// TODO temporary hack since we don't know yet where to put
		// any messages for types in another project (e.g. referenced by
		// persistence.xml)
		IFile file = this.resourcePackage.getFile();
		// 'file' will be null if the type is "external" and binary;
		// the file will be in a different project if the type is "external" and source;
		// the type will be binary if it is in a JAR in the current project
		if ((file != null) && file.getProject().equals(this.getJpaProject().getProject()) &&
				(this.resourcePackage instanceof SourceNode)) {
			// build the AST root here to pass down
			this.validate(messages, reporter, this.buildASTRoot());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaJpaContextNode#validate(java.util.List, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter,
			CompilationUnit astRoot) {
		super.validate(messages, reporter, astRoot);
		this.typeDefContainer.validate(messages, reporter, astRoot);
		this.generatorContainer.validate(messages, reporter, astRoot);
		this.queryContainer.validate(messages, reporter, astRoot);
	}
	
	@Override
	public Iterator<String> javaCompletionProposals(int pos,
			Filter<String> filter, CompilationUnit astRoot) {
		Iterator<String> result = super.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		result = this.typeDefContainer.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		result = this.generatorContainer.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		result = this.queryContainer.javaCompletionProposals(pos, filter, astRoot);
		if (result != null) {
			return result;
		}
		return null;
	}

}

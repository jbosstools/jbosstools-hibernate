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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.resource.java.source.SourceModel;
import org.eclipse.jpt.common.core.internal.utility.PlatformTools;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAnnotatedElement;
import org.eclipse.jpt.common.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackage;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.jpa.core.JpaFile;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.eclipse.jpt.jpa.core.context.PersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaGeneratorContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaContextModel;
import org.eclipse.jst.j2ee.model.internal.validation.ValidationCancelledException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePackageInfoImpl extends AbstractJavaContextModel<PersistentType.Parent> implements HibernatePackageInfo {
	
	//FIXME may be need  to create "Mapping" class??
	protected final HibernateJavaTypeDefContainer typeDefContainer;
	protected final JavaGeneratorContainer generatorContainer;
	protected final JavaQueryContainer queryContainer;

	private JavaResourcePackage resourcePackage;
	protected String name;
	
	/**
	 * @param parent
	 */
	public HibernatePackageInfoImpl(PersistentType.Parent parent, JavaResourcePackage resourcePackage) {
		super(parent);
		this.resourcePackage = resourcePackage;
		this.name = resourcePackage.getName();
		this.typeDefContainer = getJpaFactory().buildJavaTypeDefContainer(this, getResourceAnnotatedElement());
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
		this.typeDefContainer.synchronizeWithResourceModel();
		this.generatorContainer.synchronizeWithResourceModel();
		this.queryContainer.synchronizeWithResourceModel();
	}

	@Override
	public void update() {
		super.update();
		this.typeDefContainer.update();
		this.generatorContainer.update();
		this.queryContainer.update();
//		this.registerRootStructureNode();
	}
	
//	protected void registerRootStructureNode() {
//		JpaFile jpaFile = this.getJpaFile();
//		// the JPA file can be null if the resource type is "external"
//		if (jpaFile != null) {
//			jpaFile.addRootStructureNode(this.name, this);
//		}
//	}
	
	@Override
	public JptResourceType getResourceType() {
		return PlatformTools.getResourceType(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE);
	}
	
	/* Removed in keplerm6
	 * 
	 * @Override
	public boolean parentSupportsGenerators() {
		return true;
	}
	*/
	
	@Override
	public ContextType getContextType() {
		return new ContextType(this);
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
		return this.resourcePackage.getNameTextRange();
	}

	public TextRange getValidationTextRange() {
		return this.getSelectionTextRange();
	}
	
	// ********** generator container **********

	public JavaGeneratorContainer getGeneratorContainer() {
		return this.generatorContainer;
	}

	protected JavaGeneratorContainer buildGeneratorContainer() {
		return this.getJpaFactory().buildJavaGeneratorContainer(this);
	}

	// ********** query container **********

	public JavaQueryContainer getQueryContainer() {
		return this.queryContainer;
	}

	protected JavaQueryContainer buildQueryContainer() {
		return this.getJpaFactory().buildJavaQueryContainer(this);
	}
	
	protected CompilationUnit buildASTRoot() {
		return this.resourcePackage.getJavaResourceCompilationUnit().buildASTRoot();
	}

	// it would be nice if the we passed in an astRoot here, but then we
	// would need to pass it to the XML structure nodes too...
	public JpaStructureNode getStructureNode(int offset) {
		CompilationUnit astRoot = this.buildASTRoot();

		if (this.containsOffset(offset)) {
			return this;
		}
		return null;
	}
	
	public  boolean containsOffset(int offset) {
		TextRange fullTextRange = this.resourcePackage.getTextRange();
		// 'fullTextRange' will be null if the type no longer exists in the java;
		// the context model can be out of synch with the resource model
		// when a selection event occurs before the context model has a
		// chance to synch with the resource model via the update thread
		return (fullTextRange == null) ? false : fullTextRange.includes(offset);
	}

	public void dispose() {
//		this.unregisterRootStructureNode();
	}
	
//	protected void unregisterRootStructureNode() {
//		JpaFile jpaFile = this.getJpaFile();
//		// the JPA file can be null if the .java file was deleted
//		// or the resource type is "external"
//		if (jpaFile != null) {
//			jpaFile.removeRootStructureNode(this.name, this);
//		}
//	}
	
	protected JpaFile getJpaFile() {
		return this.getJpaFile(this.resourcePackage.getFile());
	}

	@Override
	public JavaResourcePackage getResourcePackage() {
		return this.resourcePackage;
	}
	
	/*
	Removed in keplerm6
	@Override
	public JpaContextModel getGeneratorContainerParent() {
		return this;  // no adapter
	}
	*/
	@Override
	public JavaResourceAnnotatedElement getResourceAnnotatedElement() {
		return this.resourcePackage;
	}
	
	@Override
	public Class<? extends JpaStructureNode> getType() {
		return JavaPackageInfo.class;
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
				(this.resourcePackage instanceof SourceModel)) {
			// build the AST root here to pass down
			this.validate(messages, reporter);
		}
	}
	
	private void doValidate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		this.typeDefContainer.validate(messages, reporter);
		this.generatorContainer.validate(messages, reporter);
		this.queryContainer.validate(messages, reporter);
	}
	
	public Iterable<String> getCompletionProposals(int pos) {
		Iterable<String> result = super.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		result = this.typeDefContainer.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		result = this.generatorContainer.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		result = this.queryContainer.getCompletionProposals(pos);
		if (result != null) {
			return result;
		}
		return null;
	}

	@Override
	public TextRange getFullTextRange() {
		return this.resourcePackage.getTextRange();
	}

	@Override
	public Iterable<? extends JpaStructureNode> getChildren() {
		return IterableTools.emptyIterable();
	}

	@Override
	public int getChildrenSize() {
		return 0;
	}

	@Override
	public void addRootStructureNodesTo(JpaFile jpaFile,
			Collection<JpaStructureNode> rootStructureNodes) {
	}

	@Override
	public boolean supportsGenerators() {
		// TODO Auto-generated method stub
		return false;
	}

	/* removed in keplerm6
	@Override
	public void gatherRootStructureNodes(JpaFile jpaFile, Collection<JpaStructureNode> rootStructureNodes) {
		IResource resource = this.getResource();
		// the resource can be null if the resource type is "external"
		if (resource != null && resource.equals(jpaFile.getFile())) {
			rootStructureNodes.add(this);
		}
	}
	*/


}

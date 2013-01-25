/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jpt.common.core.resource.xml.JptXmlResource;
import org.eclipse.jpt.common.utility.internal.iterable.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterable.TransformationIterable;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.persistence.MappingFileRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXml;
import org.eclipse.jpt.jpa.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlJavaClassRef;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class AddGeneratedClassesJob extends WorkspaceJob {
	
	private JpaProject jpaProject;
	
	private List<IResource> javaFilesToAdd;
		
	public AddGeneratedClassesJob(JpaProject jpaProject, List<IResource> javaFilesToAdd) {
		super(UIMessages.SYNC_CLASSES_JOB);
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		setRule(ruleFactory.modifyRule(jpaProject.getProject()));
		this.jpaProject = jpaProject;
		this.javaFilesToAdd = javaFilesToAdd;
	}
	
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		final SubMonitor sm = SubMonitor.convert(monitor, UIMessages.SYNC_CLASSES_TASK, 20);
		final JptXmlResource resource = jpaProject.getPersistenceXmlResource();
		if (resource == null) {
			//the resource would only be null if the persistence.xml file had an invalid content type
			return Status.OK_STATUS;
		}
		if (sm.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		sm.worked(1);
		
		XmlPersistence persistence = (XmlPersistence) resource.getRootObject();					
		XmlPersistenceUnit persistenceUnit;
		
		if (persistence.getPersistenceUnits().size() > 0) {
			persistenceUnit = persistence.getPersistenceUnits().get(0);
		}
		else {
			persistenceUnit = PersistenceFactory.eINSTANCE.createXmlPersistenceUnit();
			persistenceUnit.setName(jpaProject.getName());
			persistence.getPersistenceUnits().add(persistenceUnit);
		}
		sm.worked(1);
		
		IStatus status = addNewClassRefs(sm.newChild(17), jpaProject, persistenceUnit);

		resource.save();
		sm.done();
		return status;
	}
	
	protected IStatus addNewClassRefs(IProgressMonitor monitor, JpaProject jpaProject, XmlPersistenceUnit persistenceUnit) {
		Iterable<String> mappedClassNames = getMappedNewClassNames(jpaProject, '$');
		final SubMonitor sm = SubMonitor.convert(monitor, IterableTools.size(mappedClassNames));
		
		for (String fullyQualifiedTypeName : mappedClassNames) {
			if ( ! mappingFileContains(jpaProject, fullyQualifiedTypeName)) {
				XmlJavaClassRef classRef = PersistenceFactory.eINSTANCE.createXmlJavaClassRef();
				classRef.setJavaClass(fullyQualifiedTypeName);
				persistenceUnit.getClasses().add(classRef);
			}
			if (sm.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			sm.worked(1);
		}
		return Status.OK_STATUS;
	}

	protected Iterable<String> getMappedNewClassNames(final JpaProject jpaProject, final char enclosingTypeSeparator) {
		return new TransformationIterable<IType, String>(
				new FilteringIterable<IType>(
						new TransformationIterable<String, IType>(
								/* FIXME find correct replacement for function below 
								jpaProject.getMappedJavaSourceClassNames()) {
								*/
								jpaProject.getAnnotatedJavaSourceClassNames()) {
							@Override
							protected IType transform(String fullyQualifiedName) {
								return AddGeneratedClassesJob.this.findType(jpaProject, fullyQualifiedName);
							}
						}){
					@Override
					protected boolean accept(IType o) {
						for (IResource res : javaFilesToAdd) {
							if (res.equals(o.getResource())){
								return true;
							}
						}
						return false;
					}
				}) {
			@Override
			protected String transform(IType jdtType) {
				return jdtType.getFullyQualifiedName(enclosingTypeSeparator);
			}
		};
	}
	
	protected IType findType(JpaProject jpaProject, String typeName) {
		try {
			return jpaProject.getJavaProject().findType(typeName);
		} catch (JavaModelException ex) {
			return null;  // ignore exception?
		}
	}

	boolean mappingFileContains(JpaProject jpaProject, String fullyQualifiedTypeName) {
		PersistenceXml persistenceXml = jpaProject.getRootContextNode().getPersistenceXml();
		if (persistenceXml == null) {
			return false;
		}
		Persistence persistence = persistenceXml.getRoot();
		if (persistence == null) {
			return false;
		}
		if (persistence.getPersistenceUnitsSize() == 0) {
			return false;
		}
		PersistenceUnit persistenceUnit = persistence.getPersistenceUnit(0);
		for (MappingFileRef mappingFileRef : persistenceUnit.getMappingFileRefs()) {
			if (mappingFileRef.getPersistentType(fullyQualifiedTypeName) != null) {
				return true;
			}
		}
		return false;
	}
}
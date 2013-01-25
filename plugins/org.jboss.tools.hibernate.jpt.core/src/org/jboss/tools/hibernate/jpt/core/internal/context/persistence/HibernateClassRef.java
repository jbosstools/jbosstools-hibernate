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
package org.jboss.tools.hibernate.jpt.core.internal.context.persistence;

import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackage;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.jpa.core.context.persistence.MappingFileRef;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.persistence.GenericClassRef;
import org.eclipse.jpt.jpa.core.internal.validation.DefaultJpaValidationMessages;
import org.eclipse.jpt.jpa.core.internal.validation.JpaValidationMessages;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlJavaClassRef;
import org.eclipse.jst.j2ee.model.internal.validation.ValidationCancelledException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaPackageInfo;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateClassRef extends GenericClassRef implements PackageInfoRef{
	
	/**
	 * When <code><class>test.some.pack</class></code> is used and there is
	 * test.some.pack.package-info.java file it should be included in persistent unit.
	 * This can be <code>null</code> if the name is invalid.
	 */
	protected HibernatePackageInfo javaPackageInfo;

	
	/**
	 * Construct a <em>specified</em> class ref; i.e. a class ref with
	 * an explicit entry in the <code>persistence.xml</code>.
	 */
	public HibernateClassRef(PersistenceUnit parent, XmlJavaClassRef xmlJavaClassRef) {
		super(parent, xmlJavaClassRef);
		JavaResourcePackage resourcePackage = this.resolveJavaResourcePackage();
		if (resourcePackage != null){
			this.javaPackageInfo = this.buildJavaPackageInfo(resourcePackage);
		}
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}
	
	@Override
	protected boolean isInPackage(IPackageFragment packageFragment) {
		// FIXME recheck this method
		return super.isInPackage(packageFragment);
	}
	
	@Override
	protected String getPackageName() {
		// FIXME recheck this method
		return super.getPackageName();
	}
	
	// ********** synchronize/update **********

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		if (this.javaPackageInfo != null) {
			this.javaPackageInfo.synchronizeWithResourceModel();
		}
	}
	
	@Override
	public void update() {
		super.update();
		this.updateJavaPackageInfo();
	}
	
	// ********** java persistent type **********

	public HibernatePackageInfo getJavaPackageInfo() {
		return this.javaPackageInfo;
	}

	protected void setJavaPackageInfo(HibernatePackageInfo javaPackageInfo) {
		JavaPackageInfo old = this.javaPackageInfo;
		this.javaPackageInfo = javaPackageInfo;
		this.firePropertyChanged(JAVA_PACKAGE_INFO_PROPERTY, old, javaPackageInfo);
	}

	protected void updateJavaPackageInfo() {
		JavaResourcePackage resourcePackage = this.resolveJavaResourcePackage();
		if (resourcePackage == null) {
			if (this.javaPackageInfo != null) {
				this.javaPackageInfo.dispose();
				this.setJavaPackageInfo(null);
			}
		} else {
			if (this.javaPackageInfo == null) {
				this.setJavaPackageInfo(this.buildJavaPackageInfo(resourcePackage));
			} else {
				if (this.javaPackageInfo.getResourcePackage() == resourcePackage) {
					this.javaPackageInfo.update();
				} else {
					this.javaPackageInfo.dispose();
					this.setJavaPackageInfo(this.buildJavaPackageInfo(resourcePackage));
				}
			}
		}
	}
	
	protected String getJavaPackageInfoName() {
		return getClassName();//the same name should be used!
	}

	protected JavaResourcePackage resolveJavaResourcePackage() {
		String javaPackageInfoName = this.getJavaPackageInfoName();
		return (javaPackageInfoName == null) ? null : this.getJpaProject().getJavaResourcePackage(javaPackageInfoName);
	}

	protected HibernatePackageInfo buildJavaPackageInfo(JavaResourcePackage jrpt) {
		return this.getJpaFactory().buildJavaPackageInfo(this, jrpt);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jpt.jpa.core.internal.jpa1.context.persistence.GenericClassRef#validate(java.util.List, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void validate(List<IMessage> messages, IReporter reporter) {
		if (reporter.isCancelled()) {
			throw new ValidationCancelledException();
		}
		if (StringTools.isBlank(this.className)) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.PERSISTENCE_UNIT_UNSPECIFIED_CLASS,
					this,
					this.getValidationTextRange()
				)
			);
			return;
		}

		if (this.javaPersistentType == null
				&& this.javaPackageInfo == null) {
			messages.add(
				DefaultJpaValidationMessages.buildMessage(
					IMessage.HIGH_SEVERITY,
					JpaValidationMessages.PERSISTENCE_UNIT_NONEXISTENT_CLASS,
					new String[] {this.getJavaClassName()},
					this,
					this.getValidationTextRange()
				)
			);
			return;
		}

		if (javaPersistentType != null){
			// 190062 validate Java class only if this is the only reference to it
			// i.e. the persistence.xml ref is the only ref - none of the mapping
			// files reference the same class
			boolean validateJavaPersistentType = true;
			for (MappingFileRef mappingFileRef : this.getPersistenceUnit().getMappingFileRefsContaining(this.getJavaClassName())) {
				validateJavaPersistentType = false;
				messages.add(
						DefaultJpaValidationMessages.buildMessage(
							IMessage.LOW_SEVERITY,
							JpaValidationMessages.PERSISTENCE_UNIT_REDUNDANT_CLASS,
							new String[] {this.getJavaClassName(), mappingFileRef.getFileName()},
							this,
							this.getValidationTextRange()
						)
					);
			}

			if (validateJavaPersistentType) {
				this.validateJavaPersistentType(messages, reporter);
			}
		} else {
			validatePackageInfo(messages, reporter);
		}
	}
	
	protected void validatePackageInfo(List<IMessage> messages, IReporter reporter) {
		try {
			this.javaPackageInfo.validate(messages, reporter);
		} catch (Throwable t) {
			HibernateJptPlugin.logException(t);
		}
	}
	
}

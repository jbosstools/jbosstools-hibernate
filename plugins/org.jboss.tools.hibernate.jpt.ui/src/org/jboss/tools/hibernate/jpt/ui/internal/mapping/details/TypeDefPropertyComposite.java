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
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jpt.common.ui.internal.widgets.ClassChooserPane;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDef;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeDefPropertyComposite<T extends JavaTypeDef> extends Pane<T> {
	
	public TypeDefPropertyComposite(Pane<?> parentPane,
            PropertyValueModel<T> subjectHolder,
            Composite parent) {
		super(parentPane, subjectHolder, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeLayout(Composite container) {

		// Name widgets
		this.addLabel(container, HibernateUIMappingMessages.TypeDefPropertyComposite_Name);
		this.addText(container, buildNameTextHolder());
//		addLabeledText(
//			container,
//			HibernateUIMappingMessages.TypeDefPropertyComposite_Name,
//			buildNameTextHolder());
		
		addTypeClassChooser(container);
		addDefForTypeClassChooser2(container);

		new ParametersComposite(this, container, getSubjectHolder());
	}
	
	protected ModifiablePropertyValueModel<String> buildNameTextHolder() {
		return new PropertyAspectAdapter<JavaTypeDef, String>(
				getSubjectHolder(), JavaTypeDef.NAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getName();
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				this.subject.setName(value);
			}
		};
	}
	
	private ClassChooserPane<JavaTypeDef> addTypeClassChooser(Composite container) {

		return new ClassChooserPane<JavaTypeDef>(this, container) {
			
			private List<String> superInterfaces = Collections.singletonList(JavaTypeDef.USER_TYPE_INTERFACE);

			@Override
			protected ModifiablePropertyValueModel<String> buildTextHolder() {
				return new PropertyAspectAdapter<JavaTypeDef, String>(getSubjectHolder(), JavaTypeDef.TYPE_CLASS_PROPERTY) {
					@Override
					protected String buildValue_() {
						return this.subject.getTypeClass();
					}

					@Override
					protected void setValue_(String value) {
						if (StringTools.isBlank(value)){
							value = null;
						}
						this.subject.setTypeClass(value);
					}
				};
			}

			@Override
			protected String getClassName() {
				return getSubject().getTypeClass();
			}
			
			@Override
			protected List<String> getSuperInterfaceNames() {
				return superInterfaces;
			}

//			@Override
//			protected String getLabelText() {
//				return HibernateUIMappingMessages.TypeDefPropertyComposite_TypeClass;
//			}

			@Override
			protected IJavaProject getJavaProject() {
				return getSubject().getJpaProject().getJavaProject();
			}
			
			@Override
			protected void setClassName(String className) {
				getSubject().setTypeClass(className);
			}
			
			@Override
			protected char getEnclosingTypeSeparator() {
				return getSubject().getTypeClassEnclosingTypeSeparator();
			}
		};
	}

	private ClassChooserPane<JavaTypeDef> addDefForTypeClassChooser2(Composite container) {

		return new ClassChooserPane<JavaTypeDef>(this, container) {

			@Override
			protected ModifiablePropertyValueModel<String> buildTextHolder() {
				return new PropertyAspectAdapter<JavaTypeDef, String>(getSubjectHolder(), JavaTypeDef.DEF_FOR_TYPE_PROPERTY) {
					@Override
					protected String buildValue_() {
						return this.subject.getDefaultForTypeClass();
					}

					@Override
					protected void setValue_(String value) {
						if (StringTools.isBlank(value)){
							value = null;
						}
						this.subject.setDefaultForTypeClass(value);
					}
				};
			}

			@Override
			protected String getClassName() {
				return getSubject().getDefaultForTypeClass();
			}

//			@Override
//			protected String getLabelText() {
//				return HibernateUIMappingMessages.TypeDefPropertyComposite_DefaultForType;
//			}

			@Override
			protected IJavaProject getJavaProject() {
				return getSubject().getJpaProject().getJavaProject();
			}
			
			@Override
			protected void setClassName(String className) {
				getSubject().setDefaultForTypeClass(className);
			}
			
			@Override
			protected char getEnclosingTypeSeparator() {
				return getSubject().getTypeClassEnclosingTypeSeparator();
			}
		};
	}

}

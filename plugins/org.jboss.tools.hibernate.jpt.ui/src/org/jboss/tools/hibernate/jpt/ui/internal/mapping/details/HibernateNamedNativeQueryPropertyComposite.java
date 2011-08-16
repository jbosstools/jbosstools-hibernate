/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jpt.common.ui.internal.widgets.ClassChooserPane;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedNativeQueryPropertyComposite extends
	HibernateQueryPropertyComposite<HibernateNamedNativeQuery> {

	private ClassChooserPane<HibernateNamedNativeQuery> resultClassChooserPane;

	/**
	 * Creates a new <code>HibernateNamedNativeQueryPropertyComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 */
	public HibernateNamedNativeQueryPropertyComposite(Pane<?> parentPane,
	                                         PropertyValueModel<? extends HibernateNamedNativeQuery> subjectHolder,
	                                         Composite parent) {

		super(parentPane, subjectHolder, parent);
	}

	private ClassChooserPane<HibernateNamedNativeQuery> addClassChooser(Composite container) {

		return new ClassChooserPane<HibernateNamedNativeQuery>(this, container) {

			@Override
			protected WritablePropertyValueModel<String> buildTextHolder() {
				return new PropertyAspectAdapter<HibernateNamedNativeQuery, String>(getSubjectHolder(), HibernateNamedNativeQuery.RESULT_CLASS_PROPERTY) {
					@Override
					protected String buildValue_() {
						return this.subject.getResultClass();
					}

					@Override
					protected void setValue_(String value) {
						if (value.length() == 0) {
							value = null;
						}
						this.subject.setResultClass(value);
					}
				};
			}

			@Override
			protected String getClassName() {
				return getSubject().getResultClass();
			}

			@Override
			protected String getLabelText() {
				return JptUiDetailsMessages.NamedNativeQueryPropertyComposite_resultClass;
			}

			@Override
			protected IJavaProject getJavaProject() {
				return getSubject().getJpaProject().getJavaProject();
			}

			@Override
			protected void setClassName(String className) {
				getSubject().setResultClass(className);
			}

			@Override
			protected char getEnclosingTypeSeparator() {
				return getSubject().getResultClassEnclosingTypeSeparator();
			}
		};
	}


	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);
		this.resultClassChooserPane.enableWidgets(enabled);
	}

	@Override
	protected void initializeLayout(Composite container) {

		super.initializeLayout(container);

		// Result class chooser
		this.resultClassChooserPane = addClassChooser(container);

	}

}

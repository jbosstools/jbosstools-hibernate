/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.widgets.ClassChooserPane;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateNamedNativeQuery;

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
				return JptUiMappingsMessages.NamedNativeQueryPropertyComposite_resultClass;
			}

			@Override
			protected JpaProject getJpaProject() {
				return getSubject().getJpaProject();
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

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

package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.Index;
import org.jboss.tools.hibernate.jpt.core.internal.context.IndexHolder;

/**
 * @author Dmitry Geraskov
 *
 */
public class IndexHolderComposite extends FormPane<IndexHolder> {
	
	private WritablePropertyValueModel<Index> indexHolder;

	protected IndexHolderComposite(FormPane<? extends IndexHolder> parentPane,
			Composite parent) {
		super(parentPane, parent);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.indexHolder = buildIndexHolder();
	}

	@Override
	protected void initializeLayout(Composite container) {
		// Name widgets
		addLabeledText(
			container,
			HibernateUIMappingMessages.IndexHolderComposite_name,
			buildIndexNameHolder(),
			null//TODO add help
		);
	}

	
	protected final WritablePropertyValueModel<String> buildIndexNameHolder() {
		return new PropertyAspectAdapter<Index, String>(this.indexHolder, Index.INDEX_NAME) {
			@Override
			protected String buildValue_() {
				return subject == null ? null : subject.getName();
			}

			@Override
			public void setValue(String value) {
				if (subject != null) {
					setValue_(value);
					return;
				}
				
				if ("".equals(value)){ //$NON-NLS-1$
					return;
				}
				
				Index index = 
					(getSubject().getIndex() == null) ? getSubject().addIndex()
																: getSubject().getIndex();
				index.setName(value);
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value)) {//$NON-NLS-1$
					value = null;
				}
				subject.setName(value);
			}
		};
	}
	
	private WritablePropertyValueModel<Index> buildIndexHolder() {
		return new PropertyAspectAdapter<IndexHolder, Index>(getSubjectHolder(), IndexHolder.INDEX_PROPERTY) {
			@Override
			protected Index buildValue_() {
				return this.subject.getIndex();
			}
		};
	}
}


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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.core.context.NamedNativeQuery;
import org.eclipse.jpt.core.context.NamedQuery;
import org.eclipse.jpt.core.context.Query;
import org.eclipse.jpt.core.context.QueryContainer;
import org.eclipse.jpt.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.mappings.details.NamedNativeQueryPropertyComposite;
import org.eclipse.jpt.ui.internal.mappings.details.NamedQueryPropertyComposite;
import org.eclipse.jpt.ui.internal.util.ControlSwitcher;
import org.eclipse.jpt.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.Transformer;
import org.eclipse.jpt.utility.internal.model.value.CompositeListValueModel;
import org.eclipse.jpt.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateQueryContainer;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateQueriesComposite extends Pane<HibernateQueryContainer> {

	private AddRemoveListPane<QueryContainer> listPane;
	private NamedNativeQueryPropertyComposite namedNativeQueryPane;
	private NamedQueryPropertyComposite namedQueryPane;
	private HibernateNamedQueryPropertyComposite hibernateNamedQueryPane;
	private HibernateNamedNativeQueryPropertyComposite hibernateNamedNativeQueryPane;
	private WritablePropertyValueModel<Query> queryHolder;

	/**
	 * Creates a new <code>QueriesComposite</code>.
	 *
	 * @param parentPane The parent controller of this one
	 * @param parent The parent container
	 */
	public HibernateQueriesComposite(Pane<? extends HibernateQueryContainer> parentPane,
	                        Composite parent) {

		super(parentPane, parent, false);
	}
	
	private void addQuery() {
		addQueryFromDialog(buildAddQueryDialog());
	}
	
	protected HibernateAddQueryDialog buildAddQueryDialog() {
		return new HibernateAddQueryDialog(getControl().getShell());
	}

	protected void addQueryFromDialog(HibernateAddQueryDialog hibernateAddQueryDialog) {
		if (hibernateAddQueryDialog.open() != Window.OK) {
			return;
		}
		String queryType = hibernateAddQueryDialog.getQueryType();
		Query query;
		if (queryType == Query.NAMED_QUERY) {
			query = this.getSubject().addNamedQuery(getSubject().namedQueriesSize());
		}
		else if (queryType == Query.NAMED_NATIVE_QUERY) {
			query = this.getSubject().addNamedNativeQuery(this.getSubject().namedNativeQueriesSize());
		}
		else if (queryType == HibernateNamedQuery.HIBERNATE_NAMED_QUERY) {
			query = this.getSubject().addHibernateNamedQuery(this.getSubject().hibernateNamedQueriesSize());
		}
		else if (queryType == HibernateNamedNativeQuery.HIBERNATE_NAMED_NATIVE_QUERY) {
			query = this.getSubject().addHibernateNamedNativeQuery(this.getSubject().hibernateNamedNativeQueriesSize());
		}
		else {
			throw new IllegalArgumentException();
		}
		query.setName(hibernateAddQueryDialog.getName());
		this.queryHolder.setValue(query);//so that it gets selected in the List for the user to edit
	}

	private ListValueModel<Query> buildDisplayableQueriesListHolder() {
		return new ItemPropertyListValueModelAdapter<Query>(
			buildQueriesListHolder(),
			Query.NAME_PROPERTY
		);
	}
	
	private AddRemoveListPane<QueryContainer> addListPane(Composite container) {

		return new AddRemoveListPane<QueryContainer>(
			this,
			container,
			buildQueriesAdapter(),
			buildDisplayableQueriesListHolder(),
			this.queryHolder,
			buildQueriesListLabelProvider(),
			JpaHelpContextIds.MAPPING_NAMED_QUERIES
		);
	}

	private ListValueModel<NamedNativeQuery> buildNamedNativeQueriesListHolder() {
		return new ListAspectAdapter<QueryContainer, NamedNativeQuery>(
			getSubjectHolder(),
			QueryContainer.NAMED_NATIVE_QUERIES_LIST)
		{
			@Override
			protected ListIterator<NamedNativeQuery> listIterator_() {
				return this.subject.namedNativeQueries();
			}

			@Override
			protected int size_() {
				return this.subject.namedNativeQueriesSize();
			}
		};
	}

	private PropertyValueModel<NamedNativeQuery> buildNamedNativeQueryHolder() {
		return new TransformationPropertyValueModel<Query, NamedNativeQuery>(this.queryHolder) {
			@Override
			protected NamedNativeQuery transform_(Query value) {
				return (value instanceof NamedNativeQuery) ? (NamedNativeQuery) value : null;
			}
		};
	}
	
	private ListValueModel<HibernateNamedQuery> buildHibernateNamedQueriesListHolder() {
		return new ListAspectAdapter<QueryContainer, HibernateNamedQuery>(
			getSubjectHolder(),
			HibernateQueryContainer.HIBERNATE_NAMED_QUERIES_LIST)
		{
			@Override
			protected ListIterator<HibernateNamedQuery> listIterator_() {
				return ((HibernateQueryContainer)this.subject).hibernateNamedQueries();
			}

			@Override
			protected int size_() {
				return ((HibernateQueryContainer)this.subject).hibernateNamedQueriesSize();
			}
		};
	}
	
	private ListValueModel<HibernateNamedNativeQuery> buildHibernateNamedNativeQueriesListHolder() {
		return new ListAspectAdapter<QueryContainer, HibernateNamedNativeQuery>(
			getSubjectHolder(),
			HibernateQueryContainer.HIBERNATE_NAMED_NATIVE_QUERIES_LIST)
		{
			@Override
			protected ListIterator<HibernateNamedNativeQuery> listIterator_() {
				return ((HibernateQueryContainer)this.subject).hibernateNamedNativeQueries();
			}

			@Override
			protected int size_() {
				return ((HibernateQueryContainer)this.subject).hibernateNamedNativeQueriesSize();
			}
		};
	}

	private ListValueModel<NamedQuery> buildNamedQueriesListHolder() {
		return new ListAspectAdapter<QueryContainer, NamedQuery>(
			getSubjectHolder(),
			QueryContainer.NAMED_QUERIES_LIST)
		{
			@Override
			protected ListIterator<NamedQuery> listIterator_() {
				return this.subject.namedQueries();
			}

			@Override
			protected int size_() {
				return this.subject.namedQueriesSize();
			}
		};
	}

	private PropertyValueModel<NamedQuery> buildNamedQueryHolder() {
		return new TransformationPropertyValueModel<Query, NamedQuery>(this.queryHolder) {
			@Override
			protected NamedQuery transform_(Query value) {
				return (value instanceof NamedQuery) ? (NamedQuery) value : null;
			}
		};
	}
	
	private PropertyValueModel<HibernateNamedQuery> buildHibernateNamedQueryHolder() {
		return new TransformationPropertyValueModel<Query, HibernateNamedQuery>(this.queryHolder) {
			@Override
			protected HibernateNamedQuery transform_(Query value) {
				return (value instanceof HibernateNamedQuery) ? (HibernateNamedQuery) value : null;
			}
		};
	}
	
	private PropertyValueModel<HibernateNamedNativeQuery> buildHibernateNamedNativeQueryHolder() {
		return new TransformationPropertyValueModel<Query, HibernateNamedNativeQuery>(this.queryHolder) {
			@Override
			protected HibernateNamedNativeQuery transform_(Query value) {
				return (value instanceof HibernateNamedNativeQuery) ? (HibernateNamedNativeQuery) value : null;
			}
		};
	}

	private Transformer<Query, Control> buildPaneTransformer() {
		return new Transformer<Query, Control>() {
			public Control transform(Query query) {

				if (query == null) {
					return null;
				}
				
				if (query instanceof HibernateNamedQuery) {
					return HibernateQueriesComposite.this.hibernateNamedQueryPane.getControl();
				}
				
				if (query instanceof HibernateNamedNativeQuery){
					return HibernateQueriesComposite.this.hibernateNamedNativeQueryPane.getControl();
				}

				if (query instanceof NamedNativeQuery) {
					return HibernateQueriesComposite.this.namedNativeQueryPane.getControl();
				}

				return HibernateQueriesComposite.this.namedQueryPane.getControl();
			}
		};
	}
	
	private Adapter buildQueriesAdapter() {

		return new AddRemoveListPane.AbstractAdapter() {

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addQuery();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				for (Object item : listSelectionModel.selectedValues()) {
					if (item instanceof HibernateNamedQuery) {
						getSubject().removeHibernateNamedQuery((HibernateNamedQuery)item);
					} else if (item instanceof HibernateNamedNativeQuery) {
						getSubject().removeHibernateNamedNativeQuery((HibernateNamedNativeQuery)item);
					} else if (item instanceof NamedQuery) {
						getSubject().removeNamedQuery((NamedQuery) item);
					} else {
						getSubject().removeNamedNativeQuery((NamedNativeQuery) item);
					}
				}
			}
		};
	}

	private ListValueModel<Query> buildQueriesListHolder() {
		List<ListValueModel<? extends Query>> list = new ArrayList<ListValueModel<? extends Query>>();
		list.add(buildHibernateNamedQueriesListHolder());
		list.add(buildHibernateNamedNativeQueriesListHolder());
		list.add(buildNamedQueriesListHolder());
		list.add(buildNamedNativeQueriesListHolder());
		return new CompositeListValueModel<ListValueModel<? extends Query>, Query>(list);
	}

	private ILabelProvider buildQueriesListLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				Query query = (Query) element;
				String name = query.getName();

				if (name == null) {
					int index = -1;

					if (query instanceof HibernateNamedQuery) {
						index = CollectionTools.indexOf(getSubject().hibernateNamedQueries(), query);
					} else if (query instanceof HibernateNamedNativeQuery) {
						index = CollectionTools.indexOf(getSubject().hibernateNamedNativeQueries(), query);
					} else if (query instanceof NamedQuery) {
						index = CollectionTools.indexOf(getSubject().namedQueries(), query);
					} else {
						index = CollectionTools.indexOf(getSubject().namedNativeQueries(), query);
					}

					name = NLS.bind(JptUiMappingsMessages.QueriesComposite_displayString, index);
				}

				return name;
			}
		};
	}

	private WritablePropertyValueModel<Query> buildQueryHolder() {
		return new SimplePropertyValueModel<Query>();
	}

	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);
		this.listPane.enableWidgets(enabled);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.queryHolder = buildQueryHolder();
	}

	@Override
	protected void initializeLayout(Composite container) {

		// List pane
		this.listPane = addListPane(container);

		// Property pane
		PageBook pageBook = new PageBook(container, SWT.NULL);
		pageBook.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Hibernate Named Query property pane
		this.hibernateNamedQueryPane = new HibernateNamedQueryPropertyComposite(
			this,
			buildHibernateNamedQueryHolder(),
			pageBook
		);
		
		// Hibernate Named Native Query property pane
		this.hibernateNamedNativeQueryPane = new HibernateNamedNativeQueryPropertyComposite(
			this,
			buildHibernateNamedNativeQueryHolder(),
			pageBook
		);		
		
		// Named Query property pane
		this.namedQueryPane = new NamedQueryPropertyComposite(
			this,
			buildNamedQueryHolder(),
			pageBook
		);

		// Named Native Query property pane
		this.namedNativeQueryPane = new NamedNativeQueryPropertyComposite(
			this,
			buildNamedNativeQueryHolder(),
			pageBook
		);

		installPaneSwitcher(pageBook);
	}

	private void installPaneSwitcher(PageBook pageBook) {
		new ControlSwitcher(this.queryHolder, buildPaneTransformer(), pageBook);
	}

}

/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.common.ui.internal.util.ControlSwitcher;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.model.value.CollectionPropertyValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.CompositeListValueModel;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleCollectionValueModel;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.CollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.transformer.Transformer;
import org.eclipse.jpt.jpa.core.context.NamedNativeQuery;
import org.eclipse.jpt.jpa.core.context.NamedQuery;
import org.eclipse.jpt.jpa.core.context.Query;
import org.eclipse.jpt.jpa.core.context.QueryContainer;
import org.eclipse.jpt.jpa.core.context.java.JavaNamedNativeQuery;
import org.eclipse.jpt.jpa.core.context.java.JavaNamedQuery;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.NamedNativeQueryPropertyComposite;
import org.eclipse.jpt.jpa.ui.internal.details.NamedQueryPropertyComposite;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateQueriesComposite extends Pane<HibernateJavaQueryContainer> {

//	private AddRemoveListPane<QueryContainer, Query> listPane;
	private NamedNativeQueryPropertyComposite namedNativeQueryPane;
	private Pane<? extends Query> namedQueryPane;
	private HibernateNamedQueryPropertyComposite hibernateNamedQueryPane;
	private HibernateNamedNativeQueryPropertyComposite hibernateNamedNativeQueryPane;
//	private ModifiablePropertyValueModel<Query> queryHolder;
	private ModifiableCollectionValueModel<Query> selectedQueriesModel;
	private PropertyValueModel<Query> selectedQueryModel;

	/**
	 * Creates a new <code>QueriesComposite</code>.
	 *
	 * @param parentPane The parent controller of this one
	 * @param parent The parent container
	 */
	public HibernateQueriesComposite(Pane<?> parentPane,
			PropertyValueModel<? extends HibernateJavaQueryContainer> subjectHolder,
			Composite parent) {

				super(parentPane, subjectHolder, parent);
	}

	private Query addQuery() {
		return addQueryFromDialog(buildAddQueryDialog());
	}

	protected HibernateAddQueryDialog buildAddQueryDialog() {
		boolean hibernateOnly = (getSubject().getParent() instanceof HibernatePackageInfo);
		return new HibernateAddQueryDialog(getControl().getShell(), this.getSubject().getPersistenceUnit(), hibernateOnly);
	}

	protected Query addQueryFromDialog(HibernateAddQueryDialog hibernateAddQueryDialog) {
		if (hibernateAddQueryDialog.open() != Window.OK) {
			return null;
		}
		String queryType = hibernateAddQueryDialog.getQueryType();
		Query query;
		if (queryType == hibernateAddQueryDialog.NAMED_QUERY) {
			query = this.getSubject().addNamedQuery(getSubject().getNamedQueriesSize());
		}
		else if (queryType == hibernateAddQueryDialog.NAMED_NATIVE_QUERY) {
			query = this.getSubject().addNamedNativeQuery(this.getSubject().getNamedNativeQueriesSize());
		}
		else if (queryType == HibernateNamedQuery.HIBERNATE_NAMED_QUERY) {
			query = this.getSubject().addHibernateNamedQuery(this.getSubject().getHibernateNamedQueriesSize());
		}
		else if (queryType == HibernateNamedNativeQuery.HIBERNATE_NAMED_NATIVE_QUERY) {
			query = this.getSubject().addHibernateNamedNativeQuery(this.getSubject().getHibernateNamedNativeQueriesSize());
		}
		else {
			throw new IllegalArgumentException();
		}
		query.setName(hibernateAddQueryDialog.getName());
//		this.getQueryHolder().setValue(query);//so that it gets selected in the List for the user to edit
		return query;
	}

	private ListValueModel<Query> buildDisplayableQueriesListHolder() {
		return new ItemPropertyListValueModelAdapter<Query>(
			buildQueriesListHolder(),
			Query.NAME_PROPERTY
		);
	}

	private AddRemoveListPane<QueryContainer, Query> addListPane(Composite container) {

		return new AddRemoveListPane<QueryContainer, Query>(
			this,
			container,
			buildQueriesAdapter(),
			buildDisplayableQueriesListHolder(),
//			this.queryHolder,
			this.selectedQueriesModel,
			buildQueriesListLabelProvider(),
			JpaHelpContextIds.MAPPING_NAMED_QUERIES
		);
	}

	private ListValueModel<JavaNamedNativeQuery> buildNamedNativeQueriesListHolder() {
		return new ListAspectAdapter<JavaQueryContainer, JavaNamedNativeQuery>(
			getSubjectHolder(),
			QueryContainer.NAMED_NATIVE_QUERIES_LIST)
		{
			@Override
			protected ListIterator<JavaNamedNativeQuery> listIterator_() {
				return this.subject.getNamedNativeQueries().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getNamedNativeQueriesSize();
			}
		};
	}

	private PropertyValueModel<NamedNativeQuery> buildNamedNativeQueryHolder() {
		return new TransformationPropertyValueModel<Query, NamedNativeQuery>(this.selectedQueryModel) {
			@Override
			protected NamedNativeQuery transform_(Query value) {
				return (value instanceof NamedNativeQuery) ? (NamedNativeQuery) value : null;
			}
		};
	}

	private ListValueModel<HibernateJavaNamedQuery> buildHibernateNamedQueriesListHolder() {
		return new ListAspectAdapter<HibernateJavaQueryContainer, HibernateJavaNamedQuery>(
			getSubjectHolder(),
			HibernateJavaQueryContainer.HIBERNATE_NAMED_QUERIES_LIST)
		{
			@Override
			protected ListIterator<HibernateJavaNamedQuery> listIterator_() {
				return this.subject.getHibernateNamedQueries().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getHibernateNamedQueriesSize();
			}
		};
	}

	private ListValueModel<HibernateJavaNamedNativeQuery> buildHibernateNamedNativeQueriesListHolder() {
		return new ListAspectAdapter<HibernateJavaQueryContainer, HibernateJavaNamedNativeQuery>(
			getSubjectHolder(),
			HibernateJavaQueryContainer.HIBERNATE_NAMED_NATIVE_QUERIES_LIST)
		{
			@Override
			protected ListIterator<HibernateJavaNamedNativeQuery> listIterator_() {
				return this.subject.getHibernateNamedNativeQueries().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getHibernateNamedNativeQueriesSize();
			}
		};
	}

	private ListValueModel<JavaNamedQuery> buildNamedQueriesListHolder() {
		return new ListAspectAdapter<HibernateJavaQueryContainer, JavaNamedQuery>(
			getSubjectHolder(),
			QueryContainer.NAMED_QUERIES_LIST)
		{
			@Override
			protected ListIterator<JavaNamedQuery> listIterator_() {
				return this.subject.getNamedQueries().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getNamedQueriesSize();
			}
		};
	}

	private PropertyValueModel<NamedQuery> buildNamedQueryHolder() {
		return new TransformationPropertyValueModel<Query, NamedQuery>(this.selectedQueryModel) {
			@Override
			protected NamedQuery transform_(Query value) {
				return (value instanceof NamedQuery) ? (NamedQuery) value : null;
			}
		};
	}

	private PropertyValueModel<HibernateNamedQuery> buildHibernateNamedQueryHolder() {
		return new TransformationPropertyValueModel<Query, HibernateNamedQuery>(this.selectedQueryModel) {
			@Override
			protected HibernateNamedQuery transform_(Query value) {
				return (value instanceof HibernateNamedQuery) ? (HibernateNamedQuery) value : null;
			}
		};
	}

	private PropertyValueModel<HibernateNamedNativeQuery> buildHibernateNamedNativeQueryHolder() {
		return new TransformationPropertyValueModel<Query, HibernateNamedNativeQuery>(this.selectedQueryModel) {
			@Override
			protected HibernateNamedNativeQuery transform_(Query value) {
				return (value instanceof HibernateNamedNativeQuery) ? (HibernateNamedNativeQuery) value : null;
			}
		};
	}

	private Transformer<Query, Control> buildPaneTransformer() {
		return new Transformer<Query, Control>() {
			@Override
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

	private Adapter<Query> buildQueriesAdapter() {

		return new AddRemoveListPane.AbstractAdapter<Query>() {

			@Override
			public Query addNewItem() {
				return addQuery();
			}

			@Override
			public void removeSelectedItems(
					CollectionValueModel<Query> selectedItemsModel) {
				Iterator<Query> iterator = selectedItemsModel.iterator();
				while (iterator.hasNext()) {
					Query item = iterator.next();
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
		return CompositeListValueModel.forModels(list);
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
						index = IterableTools.indexOf(getSubject().getHibernateNamedQueries(), query);
					} else if (query instanceof HibernateNamedNativeQuery) {
						index = IterableTools.indexOf(getSubject().getHibernateNamedNativeQueries(), query);
					} else if (query instanceof NamedQuery) {
						index = IterableTools.indexOf(getSubject().getNamedQueries(), query);
					} else {
						index = IterableTools.indexOf(getSubject().getNamedNativeQueries(), query);
					}

					name = NLS.bind(JptUiDetailsMessages.QueriesComposite_displayString, index);
				}

				return name;
			}
		};
	}

//	private ModifiablePropertyValueModel<Query> buildQueryHolder() {
//		return new SimplePropertyValueModel<Query>();
//	}

//	@Override
//	public void enableWidgets(boolean enabled) {
//		this.getEnabledModel().set
//		super.enableWidgets(enabled);
//		this.listPane.enableWidgets(enabled);
//	}

	@Override
	protected void initialize() {
		super.initialize();
		this.selectedQueriesModel = this.buildSelectedQueriesModel();
		this.selectedQueryModel = this.buildSelectedQueryModel(this.selectedQueriesModel);
//		this.queryHolder = buildQueryHolder();
	}

	private ModifiableCollectionValueModel<Query> buildSelectedQueriesModel() {
		return new SimpleCollectionValueModel<Query>();
	}

	private PropertyValueModel<Query> buildSelectedQueryModel(CollectionValueModel<Query> selectedQueriesModel) {
		return new CollectionPropertyValueModelAdapter<Query, Query>(selectedQueriesModel) {
			@Override
			protected Query buildValue() {
				if (this.collectionModel.size() == 1) {
					return this.collectionModel.iterator().next();
				}
				return null;
			}
		};
	}

	@Override
	protected void initializeLayout(Composite container) {

		// List pane
		addListPane(container);
//		this.listPane = addListPane(container);

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
		this.namedQueryPane = this.buildNamedQueryPropertyComposite(pageBook);

		// Named Native Query property pane
		this.namedNativeQueryPane = new NamedNativeQueryPropertyComposite(
			this,
			buildNamedNativeQueryHolder(),
			pageBook
		);

		installPaneSwitcher(pageBook);
	}
	
	protected Pane<? extends NamedQuery> buildNamedQueryPropertyComposite(PageBook pageBook) {
		return new NamedQueryPropertyComposite<NamedQuery>(
			this,
			this.buildNamedQueryHolder(),
			pageBook
		);
	}

	private void installPaneSwitcher(PageBook pageBook) {
//		new ControlSwitcher(this.queryHolder, buildPaneTransformer(), pageBook);
		new ControlSwitcher(getQueryHolder(), buildPaneTransformer(), pageBook);
	}

	protected PropertyValueModel<Query> getQueryHolder() {
		return this.selectedQueryModel;
	}

}

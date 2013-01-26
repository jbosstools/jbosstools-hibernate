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
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jpt.common.ui.internal.widgets.DialogPane;
import org.eclipse.jpt.common.ui.internal.widgets.ValidatingDialog;
import org.eclipse.jpt.common.utility.internal.StringTools;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.StaticListValueModel;
import org.eclipse.jpt.common.utility.internal.node.AbstractNode;
import org.eclipse.jpt.common.utility.node.Node;
import org.eclipse.jpt.common.utility.node.Problem;
import org.eclipse.jpt.common.utility.internal.transformer.AbstractTransformer;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.transformer.Transformer;
import org.eclipse.jpt.jpa.core.context.Query;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateAddQueryDialog extends ValidatingDialog<AddQueryStateObject> {

	public static final String NAMED_QUERY = "namedQuery"; //$NON-NLS-1$
	public static final String NAMED_NATIVE_QUERY = "namedNativeQuery"; //$NON-NLS-1$
	
	private boolean hibernateOnly;


	/**
	 * The associated persistence unit
	 */
	private PersistenceUnit pUnit;
	// ********** constructors **********

	/**
	 * Use this constructor to edit an existing conversion value
	 * @param hibernateOnly shows that only @org.hibernate.annotations.NamedQuery
	 * 						and @org.hibernate.annotations.NamedNativeQuery
	 * 						could be added.
	 */
	public HibernateAddQueryDialog(Shell parent, PersistenceUnit pUnit, boolean hibernateOnly) {
		super(parent);
		this.pUnit = pUnit;
		this.hibernateOnly = hibernateOnly;
	}

	@Override
	protected AddQueryStateObject buildStateObject() {
		return new AddQueryStateObject(this.pUnit);
	}

	// ********** open **********

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(this.getTitle());
	}

	@Override
	protected String getTitle() {
		return JptUiDetailsMessages.AddQueryDialog_title;
	}

	@Override
	protected String getDescriptionTitle() {
		return JptUiDetailsMessages.AddQueryDialog_descriptionTitle;
	}

	@Override
	protected String getDescription() {
		return JptUiDetailsMessages.AddQueryDialog_description;
	}

	@Override
	protected DialogPane<AddQueryStateObject> buildLayout(Composite container) {
		return new QueryDialogPane(container);
	}

	@Override
	public void create() {
		super.create();

		QueryDialogPane pane = (QueryDialogPane) getPane();
		pane.selectAll();

		getButton(OK).setEnabled(false);
	}


	// ********** public API **********

	/**
	 * Return the data value set in the text widget.
	 */
	public String getName() {
		return getSubject().getName();
	}

	/**
	 * Return the object value set in the text widget.
	 */
	public String getQueryType() {
		return getSubject().getQueryType();
	}

	private class QueryDialogPane extends DialogPane<AddQueryStateObject> {

		private Text nameText;

		QueryDialogPane(Composite parent) {
			super(HibernateAddQueryDialog.this.getSubjectHolder(), parent);
		}

		@Override
		protected void initializeLayout(Composite container) {
			
			this.addLabel(container, JptUiDetailsMessages.AddQueryDialog_name);
			this.addText(container, buildNameHolder());
//			this.nameText = addLabeledText(
//				container,
//				JptUiDetailsMessages.AddQueryDialog_name,
//				buildNameHolder()
//			);

			this.addLabel(container, JptUiDetailsMessages.AddQueryDialog_queryType);
			this.addCombo(
					container,
					buildQueryTypeListHolder(), 
					buildQueryTypeHolder(), 
					buildStringConverter(),
					(String)null);
//			addLabeledCombo(
//				container,
//				JptUiDetailsMessages.AddQueryDialog_queryType,
//				buildQueryTypeListHolder(),
//				buildQueryTypeHolder(),
//				buildStringConverter(),
//				null);
		}

		protected ListValueModel<String> buildQueryTypeListHolder() {
			List<String> queryTypes = new ArrayList<String>();
			if (!hibernateOnly){
				queryTypes.add(NAMED_QUERY);
				queryTypes.add(NAMED_NATIVE_QUERY);
			}
			queryTypes.add(HibernateNamedQuery.HIBERNATE_NAMED_QUERY);
			queryTypes.add(HibernateNamedNativeQuery.HIBERNATE_NAMED_NATIVE_QUERY);
			return new StaticListValueModel<String>(queryTypes);
		}

		private Transformer<String,String> buildStringConverter() {
			return new AbstractTransformer<String,String>() {
				@Override
				protected String transform_(String value) {
					if (value == NAMED_QUERY) {
						return JptUiDetailsMessages.AddQueryDialog_namedQuery;
					}
					if (value == NAMED_NATIVE_QUERY) {
						return JptUiDetailsMessages.AddQueryDialog_namedNativeQuery;
					}
					if (value == HibernateNamedQuery.HIBERNATE_NAMED_QUERY) {
						return HibernateUIMappingMessages.HibernateAddQueryDialog_hibernateNamedQuery;
					}
					if (value == HibernateNamedNativeQuery.HIBERNATE_NAMED_NATIVE_QUERY) {
						return HibernateUIMappingMessages.HibernateAddQueryDialog_hibernateNamedNativeQuery;
					}
					return value;
				}
			};
		}

		private ModifiablePropertyValueModel<String> buildNameHolder() {
			return new PropertyAspectAdapter<AddQueryStateObject, String>(getSubjectHolder(), AddQueryStateObject.NAME_PROPERTY) {
				@Override
				protected String buildValue_() {
					return this.subject.getName();
				}

				@Override
				protected void setValue_(String value) {
					this.subject.setName(value);
				}
			};
		}

		private ModifiablePropertyValueModel<String> buildQueryTypeHolder() {
			return new PropertyAspectAdapter<AddQueryStateObject, String>(getSubjectHolder(), AddQueryStateObject.QUERY_TYPE_PROPERTY) {
				@Override
				protected String buildValue_() {
					return this.subject.getQueryType();
				}

				@Override
				protected void setValue_(String value) {
					this.subject.setQueryType(value);
				}
			};
		}

		void selectAll() {
			this.nameText.selectAll();
		}
	}
}

final class AddQueryStateObject extends AbstractNode
{
	/**
	 * The initial name or <code>null</code>
	 */
	private String name;

	/**
	 * The initial queryType or <code>null</code>
	 */
	private String queryType;

	/**
	 * The <code>Validator</code> used to validate this state object.
	 */
	private Validator validator;

	/**
	 * The associated persistence unit
	 */
	private PersistenceUnit pUnit;

	/**
	 * Notifies a change in the data value property.
	 */
	static final String NAME_PROPERTY = "nameProperty"; //$NON-NLS-1$

	/**
	 * Notifies a change in the query type property.
	 */
	static final String QUERY_TYPE_PROPERTY = "queryTypeProperty"; //$NON-NLS-1$

	/**
	 * Creates a new <code>NewNameStateObject</code>.
	 *
	 * @param name The initial input or <code>null</code> if no initial value can
	 * be specified
	 * @param names The collection of names that can't be used or an empty
	 * collection if none are available
	 */
	AddQueryStateObject(PersistenceUnit pUnit) {
		super(null);
		this.pUnit = pUnit;
	}

	private void addNameProblemsTo(List<Problem> currentProblems) {
		if (StringTools.isBlank(this.name)) {
			currentProblems.add(buildProblem(JptUiDetailsMessages.QueryStateObject_nameMustBeSpecified, IMessageProvider.ERROR));
		}
		else if (names().contains(this.name)){
			currentProblems.add(buildProblem(JptUiDetailsMessages.AddQueryDialog_nameExists, IMessageProvider.WARNING));
		}
	}

	private void addQueryTypeProblemsTo(List<Problem> currentProblems) {
		if (StringTools.isBlank(this.queryType)) {
			currentProblems.add(buildProblem(JptUiDetailsMessages.QueryStateObject_typeMustBeSpecified, IMessageProvider.ERROR));
		}
	}

	@Override
	protected void addProblemsTo(List<Problem> currentProblems) {
		super.addProblemsTo(currentProblems);
		addNameProblemsTo(currentProblems);
		addQueryTypeProblemsTo(currentProblems);
	}

	private List<String> names(){
		List<String> names = new ArrayList<String>();
		for (Query query : this.pUnit.getQueries()) {
			names.add(query.getName());
		}
		return names;
}

	@Override
	protected void checkParent(Node parentNode) {
		//no parent
	}

	@Override
	public String displayString() {
		return null;
	}

	String getName() {
		return this.name;
	}

	String getQueryType() {
		return this.queryType;
	}

	public void setName(String newName) {
		String oldName = this.name;
		this.name = newName;
		firePropertyChanged(NAME_PROPERTY, oldName, newName);
	}

	public void setQueryType(String newQueryType) {
		String old = this.queryType;
		this.queryType = newQueryType;
		firePropertyChanged(QUERY_TYPE_PROPERTY, old, newQueryType);
	}

	@Override
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Validator getValidator() {
		return this.validator;
	}
}


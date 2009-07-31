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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jpt.core.context.Query;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.widgets.DialogPane;
import org.eclipse.jpt.ui.internal.widgets.ValidatingDialog;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.StringTools;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.StaticListValueModel;
import org.eclipse.jpt.utility.internal.node.AbstractNode;
import org.eclipse.jpt.utility.internal.node.Node;
import org.eclipse.jpt.utility.internal.node.Problem;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
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
	
	// ********** constructors **********

	/**
	 * Use this constructor to edit an existing conversion value
	 */
	public HibernateAddQueryDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected AddQueryStateObject buildStateObject() {
		return new AddQueryStateObject();
	}

	// ********** open **********

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(this.getTitle());
	}

	@Override
	protected String getTitle() {
		return JptUiMappingsMessages.AddQueryDialog_title;
	}

	@Override
	protected String getDescriptionTitle() {
		return JptUiMappingsMessages.AddQueryDialog_descriptionTitle;
	}
	
	@Override
	protected String getDescription() {
		return JptUiMappingsMessages.AddQueryDialog_description;
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
			this.nameText = addLabeledText(
				container,
				JptUiMappingsMessages.AddQueryDialog_name,
				buildNameHolder()
			);
			
			addLabeledCombo(
				container, 
				JptUiMappingsMessages.AddQueryDialog_queryType, 
				buildQueryTypeListHolder(), 
				buildQueryTypeHolder(), 
				buildStringConverter(),
				null);
		}

		protected ListValueModel<String> buildQueryTypeListHolder() {
			List<String> queryTypes = new ArrayList<String>();
			queryTypes.add(Query.NAMED_QUERY);
			queryTypes.add(Query.NAMED_NATIVE_QUERY);
			queryTypes.add(HibernateNamedQuery.HIBERNATE_NAMED_QUERY);
			queryTypes.add(HibernateNamedNativeQuery.HIBERNATE_NAMED_NATIVE_QUERY);			
			return new StaticListValueModel<String>(queryTypes);
		}
		
		private StringConverter<String> buildStringConverter() {
			return new StringConverter<String>() {
				public String convertToString(String value) {
					if (value == Query.NAMED_QUERY) {
						return JptUiMappingsMessages.AddQueryDialog_namedQuery;
					}
					if (value == Query.NAMED_NATIVE_QUERY) {
						return JptUiMappingsMessages.AddQueryDialog_namedNativeQuery;
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
		
		private WritablePropertyValueModel<String> buildNameHolder() {
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

		private WritablePropertyValueModel<String> buildQueryTypeHolder() {
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
	AddQueryStateObject() {
		super(null);

	}

	private void addNameProblemsTo(List<Problem> currentProblems) {
		if (StringTools.stringIsEmpty(this.name)) {
			currentProblems.add(buildProblem(JptUiMappingsMessages.QueryStateObject_nameMustBeSpecified));
		}
	}

	private void addQueryTypeProblemsTo(List<Problem> currentProblems) {
		if (StringTools.stringIsEmpty(this.queryType)) {
			currentProblems.add(buildProblem(JptUiMappingsMessages.QueryStateObject_typeMustBeSpecified));
		}
	}

	@Override
	protected void addProblemsTo(List<Problem> currentProblems) {
		super.addProblemsTo(currentProblems);
		addNameProblemsTo(currentProblems);
		addQueryTypeProblemsTo(currentProblems);
	}

	@Override
	protected void checkParent(Node parentNode) {
		//no parent
	}

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


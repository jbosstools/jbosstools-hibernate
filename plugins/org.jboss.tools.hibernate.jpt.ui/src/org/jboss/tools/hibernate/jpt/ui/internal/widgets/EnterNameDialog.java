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
package org.jboss.tools.hibernate.jpt.ui.internal.widgets;

import java.util.Collection;

import org.eclipse.jpt.ui.internal.widgets.DialogPane;
import org.eclipse.jpt.ui.internal.widgets.ValidatingDialog;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateUIMappingMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class EnterNameDialog extends ValidatingDialog<NameStateObject> {
	
	private String description;
	private Image descriptionImage;
	private String descriptionTitle;
	private String labelText;
	private String name;
	private Collection<String> names;
	
	public EnterNameDialog(Shell parentShell, String descriptionTitle){
		this(parentShell, descriptionTitle, null, null);
	}
	
	public EnterNameDialog(Shell parentShell, String descriptionTitle, String name,
            Collection<String> names){
		this(parentShell,
			HibernateUIMappingMessages.EnterNameDialog_title,
			descriptionTitle,
			null,
			null,
			HibernateUIMappingMessages.EnterNameDialog_labelText,
			name,
			names);
	}
	
	/**
	 * Creates a new <code>EnterNameDialog</code>.
	 *
	 */
	public EnterNameDialog(Shell parentShell,
	              String dialogTitle,
	              String descriptionTitle,
	              Image descriptionImage,
	              String description,
	              String labelText,
	              String name,
	              Collection<String> names)
	{
		super(parentShell, dialogTitle);

		this.name             = name;
		this.labelText        = labelText;
		this.description      = description;
		this.descriptionImage = descriptionImage;
		this.descriptionTitle = descriptionTitle;
		this.names 			  = names;
	}
	
	@Override
	protected DialogPane<NameStateObject> buildLayout(Composite container) {
		return new NewNameDialogPane(container);
	}

	@Override
	protected NameStateObject buildStateObject() {
		return new NameStateObject(name, names);
	}

	@Override
	public void create() {
		super.create();

		NewNameDialogPane pane = (NewNameDialogPane) getPane();
		pane.selectAll();

		getButton(OK).setEnabled(false);
	}

	@Override
	protected String getDescription() {
		return description;
	}

	@Override
	protected Image getDescriptionImage() {
		return descriptionImage;
	}

	@Override
	protected String getDescriptionTitle() {
		return descriptionTitle;
	}

	/**
	 * Returns the text field's input, which is the new name the user entered.
	 *
	 * @return The name the user entered
	 */
	public String getName() {
		return getSubject().getName();
	}

	private class NewNameDialogPane extends DialogPane<NameStateObject> {

		private Text text;

		NewNameDialogPane(Composite parent) {
			super(EnterNameDialog.this.getSubjectHolder(), parent);
		}

		private WritablePropertyValueModel<String> buildNameHolder() {
			return new PropertyAspectAdapter<NameStateObject, String>(getSubjectHolder(), NameStateObject.NAME_PROPERTY) {
				@Override
				protected String buildValue_() {
					return subject.getName();
				}

				@Override
				protected void setValue_(String value) {
					subject.setName(value);
				}
			};
		}

		@Override
		protected void initializeLayout(Composite container) {

			text = addLabeledText(
				container,
				labelText,
				buildNameHolder()
			);
		}

		void selectAll() {
			text.selectAll();
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2008, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     
 *  Moved here by Dmitry Geraskov. Combobox marked as protected.
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl;

import java.util.Iterator;

import org.eclipse.jpt.core.JpaDataSource;
import org.eclipse.jpt.core.JpaNode;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.db.Catalog;
import org.eclipse.jpt.db.Column;
import org.eclipse.jpt.db.ConnectionListener;
import org.eclipse.jpt.db.ConnectionProfile;
import org.eclipse.jpt.db.Database;
import org.eclipse.jpt.db.ForeignKey;
import org.eclipse.jpt.db.Schema;
import org.eclipse.jpt.db.Sequence;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.Tracing;
import org.eclipse.jpt.ui.internal.listeners.SWTConnectionListenerWrapper;
import org.eclipse.jpt.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.util.SWTUtil;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.StringTools;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This abstract pane keeps a combo in sync with the database objects
 * when a connection is active.
 *
 * @see CatalogCombo
 * @see ColumnCombo
 * @see SchemaCombo
 * @see SequenceCombo
 * @see TableCombo
 */
@SuppressWarnings("nls")
public abstract class DatabaseObjectCombo<T extends JpaNode>
	extends Pane<T>
{

	/**
	 * The main (only) widget of this pane.
	 */
	protected CCombo comboBox;

	/**
	 * The listener added to the <code>ConnectionProfile</code>.
	 * It keeps the combo in sync with the database metadata.
	 */
	private ConnectionListener connectionListener;

	private PropertyChangeListener connectionProfileListener;
	
	// ********** constructors **********

	protected DatabaseObjectCombo(
						Pane<? extends T> parentPane,
						Composite parent
	) {
		super(parentPane, parent);
	}

	protected DatabaseObjectCombo(
						Pane<?> parentPane,
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent
	) {
		super(parentPane, subjectHolder, parent);
	}

	protected DatabaseObjectCombo(
						PropertyValueModel<? extends T> subjectHolder,
						Composite parent,
						WidgetFactory widgetFactory
	) {
		super(subjectHolder, parent, widgetFactory);
	}


	// ********** initialization **********

	@Override
	protected void initialize() {
		super.initialize();
		this.connectionListener = this.buildConnectionListener();
		this.connectionProfileListener = this.buildConnectionProfileListener();
	}

	protected ConnectionListener buildConnectionListener() {
		return new SWTConnectionListenerWrapper(this.buildConnectionListener_());
	}

	protected ConnectionListener buildConnectionListener_() {
		return new LocalConnectionListener();
	}
	
	protected PropertyChangeListener buildConnectionProfileListener() {
		return new SWTPropertyChangeListenerWrapper(this.buildConnectionProfileListener_());
	}

	protected PropertyChangeListener buildConnectionProfileListener_() {
		return new PropertyChangeListener(){
		
			public void propertyChanged(PropertyChangeEvent event) {
				connectionProfileChanged(event);
			}
		};
	}

	protected void connectionProfileChanged(PropertyChangeEvent event) {
		if (event.getOldValue() != null) {
			((ConnectionProfile) event.getOldValue()).removeConnectionListener(this.connectionListener);
		}
		if (event.getNewValue() != null) {
			((ConnectionProfile) event.getNewValue()).addConnectionListener(this.connectionListener);			
		}
		this.repopulateComboBox();
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		this.comboBox = this.addEditableCCombo(container);
		this.comboBox.addModifyListener(this.buildModifyListener());
		SWTUtil.attachDefaultValueHandler(this.comboBox);
	}

	protected ModifyListener buildModifyListener() {
		return new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				DatabaseObjectCombo.this.comboBoxModified();
			}
		};
	}


	// ********** abstract methods **********

	/**
	 * Return the possible values to be added to the combo during
	 * population.
	 */
	protected abstract Iterator<String> values();

	/**
	 * Return the default value, or <code>null</code> if no default is
	 * specified. This method is only called when the subject is non-null.
	 */
	protected abstract String getDefaultValue();

	/**
	 * Return the current value from the subject.
	 * This method is only called when the subject is non-null.
	 */
	protected abstract String getValue();

	/**
	 * Set the specified value as the new value on the subject.
	 */
	protected abstract void setValue(String value);


	// ********** overrides **********

	@Override
	protected void engageListeners_(T subject) {
		super.engageListeners_(subject);

		subject.getJpaProject().getDataSource().addPropertyChangeListener(JpaDataSource.CONNECTION_PROFILE_PROPERTY, this.connectionProfileListener);
		ConnectionProfile cp = subject.getJpaProject().getConnectionProfile();
		if (cp != null) {
			cp.addConnectionListener(this.connectionListener);
		}
	}

	@Override
	protected void disengageListeners_(T subject) {
		ConnectionProfile cp = subject.getJpaProject().getConnectionProfile();
		if (cp != null) {
			cp.removeConnectionListener(this.connectionListener);
		}
		subject.getJpaProject().getDataSource().removePropertyChangeListener(JpaDataSource.CONNECTION_PROFILE_PROPERTY, this.connectionProfileListener);

		super.disengageListeners_(subject);
	}

	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);

		if ( ! this.comboBox.isDisposed()) {
			this.comboBox.setEnabled(enabled);
		}
	}

	@Override
	protected void propertyChanged(String propertyName) {
		super.propertyChanged(propertyName);
		this.updateSelectedItem();
	}

	@Override
	protected void doPopulate() {
		super.doPopulate();
		this.populateComboBox();
	}


	// ********** populating **********

	/**
	 * Populate the combo-box list by clearing it, then adding first the default
	 * value, if available, and then the possible choices.
	 */
	protected void populateComboBox() {
		this.comboBox.removeAll();

		this.comboBox.add(this.buildDefaultValueEntry());

		if (this.connectionProfileIsActive()) {
			for (Iterator<String> stream = this.values(); stream.hasNext(); ) {
				this.comboBox.add(stream.next());
			}
		}

		this.updateSelectedItem_();
	}

	protected String buildDefaultValueEntry() {
		if (getSubject() == null) {
			return JptUiMappingsMessages.NoneSelected;
		}
		String defaultValue = this.getDefaultValue();
		return (defaultValue == null) ? this.buildNullDefaultValueEntry() : this.buildNonNullDefaultValueEntry(defaultValue);
	}

	protected String buildNullDefaultValueEntry() {
		return JptUiMappingsMessages.DefaultEmpty;
	}

	protected String buildNonNullDefaultValueEntry(String defaultValue) {
		return NLS.bind(
						JptUiMappingsMessages.DefaultWithOneParam,
						defaultValue
					);
	}

	protected void updateSelectedItem() {
		// make sure the default value is up to date (??? ~bjv)
		String defaultValueEntry = this.buildDefaultValueEntry();
		if ( ! this.comboBox.getItem(0).equals(defaultValueEntry)) {
			this.comboBox.remove(0);
			this.comboBox.add(defaultValueEntry, 0);
		}

		this.updateSelectedItem_();
	}

	/**
	 * Updates the selected item by selecting the current value, if not
	 * <code>null</code>, or select the default value if one is available,
	 * otherwise remove the selection.
	 */
	protected void updateSelectedItem_() {
		String value = (this.getSubject() == null) ? null : this.getValue();
		if (value == null) {
			// select the default value
			this.comboBox.select(0);
		} else {
			// select the new value
			if ( ! value.equals(this.comboBox.getText())) {
				// This prevents the cursor from being set back to the beginning of the line (bug 234418).
				// The reason we are hitting this method at all is because the
				// context model is updating from the resource model in a way
				// that causes change notifications to be fired (the annotation
				// is added to the resource model, change notification occurs
				// on the update thread, and then the name is set, these 2
				// threads can get in the wrong order).
				// The #valueChanged() method sets the populating flag to true,
				// but in this case it is already set back to false when we
				// receive notification back from the model because it has
				// moved to the update thread and then jumps back on the UI thread.
				this.comboBox.setText(value);
			}
		}
	}


	// ********** combo-box listener callback **********

	protected void comboBoxModified() {
		if ( ! this.isPopulating()) {
			this.valueChanged(this.comboBox.getText());
		}
	}

	/**
	 * The combo-box selection has changed, update the model if necessary.
	 * If the value has changed and the subject is null, we can build a subject
	 * before setting the value.
	 */
	protected void valueChanged(String newValue) {
		JpaNode subject = this.getSubject();
		String oldValue;
		if (subject == null) {
			if (this.nullSubjectIsNotAllowed()) {
				return;  // no subject to set the value on
			}
			oldValue = null;
		} else {
			oldValue = this.getValue();
		}

		// convert empty string or default to null
		if (StringTools.stringIsEmpty(newValue) || this.valueIsDefault(newValue)) {
			newValue = null;
		}

		// set the new value if it is different from the old value
		if (this.valuesAreDifferent(oldValue, newValue)) {
			this.setPopulating(true);

			try {
				this.setValue(newValue);
			} finally {
				this.setPopulating(false);
			}
		}
	}

	/**
	 * Return whether we can set the value when the subject is null
	 * (i.e. #setValue(String) will construct the subject if necessary).
	 */
	protected boolean nullSubjectIsAllowed() {
		return false;
	}

	protected final boolean nullSubjectIsNotAllowed() {
		return ! this.nullSubjectIsAllowed();
	}

	/**
	 * pre-condition: value is not null
	 */
	protected boolean valueIsDefault(String value) {
		return (this.comboBox.getItemCount() > 0)
				&& value.equals(this.comboBox.getItem(0));
	}

	protected boolean valuesAreEqual(String value1, String value2) {
		if ((value1 == null) && (value2 == null)) {
			return true;	// both are null
		}
		if ((value1 == null) || (value2 == null)) {
			return false;	// one is null but the other is not
		}
		return value1.equals(value2);
	}

	protected boolean valuesAreDifferent(String value1, String value2) {
		return ! this.valuesAreEqual(value1, value2);
	}


	// ********** convenience methods **********

	/**
	 * Return the subject's JPA project.
	 * Allow subclasses to override this method, so we can still get the JPA
	 * project even when the subject is null.
	 */
	protected JpaProject getJpaProject() {
		T subject = this.getSubject();
		return (subject == null) ? null : subject.getJpaProject();
	}

	/**
	 * Return the subject's connection profile.
	 */
	protected final ConnectionProfile getConnectionProfile() {
		JpaProject jpaProject = this.getJpaProject();
		return (jpaProject == null) ? null : jpaProject.getConnectionProfile();
	}

	/**
	 * Return whether the subject's connection profile is active.
	 */
	protected final boolean connectionProfileIsActive() {
		ConnectionProfile cp = this.getConnectionProfile();
		return (cp != null) && cp.isActive();
	}

	/**
	 * Returns the subject's database.
	 */
	protected final Database getDatabase() {
		ConnectionProfile cp = this.getConnectionProfile();
		return (cp == null) ? null : cp.getDatabase();
	}


	// ********** connection listener callbacks **********

	protected void repopulateComboBox() {
		if ( ! this.comboBox.isDisposed()) {
			this.repopulate();
		}
	}

	protected final void databaseChanged(Database database) {
		if ( ! this.comboBox.isDisposed()) {
			this.databaseChanged_(database);
		}
	}

	protected void databaseChanged_(Database database) {
		// do nothing by default
	}

	protected final void catalogChanged(Catalog catalog) {
		if ( ! this.comboBox.isDisposed()) {
			this.catalogChanged_(catalog);
		}
	}

	protected void catalogChanged_(Catalog catalog) {
		// do nothing by default
	}

	protected final void schemaChanged(Schema schema) {
		if ( ! this.comboBox.isDisposed()) {
			this.schemaChanged_(schema);
		}
	}

	protected void schemaChanged_(Schema schema) {
		// do nothing by default
	}

	protected final void sequenceChanged(Sequence sequence) {
		if ( ! this.comboBox.isDisposed()) {
			this.sequenceChanged_(sequence);
		}
	}

	protected void sequenceChanged_(Sequence sequence) {
		// do nothing by default
	}

	protected final void tableChanged(Table table) {
		if ( ! this.comboBox.isDisposed()) {
			this.tableChanged_(table);
		}
	}

	protected void tableChanged_(Table table) {
		// do nothing by default
	}

	protected final void columnChanged(Column column) {
		if ( ! this.comboBox.isDisposed()) {
			this.columnChanged_(column);
		}
	}

	protected void columnChanged_(Column column) {
		// do nothing by default
	}

	protected final void foreignKeyChanged(ForeignKey foreignKey) {
		if ( ! this.comboBox.isDisposed()) {
			this.foreignKeyChanged_(foreignKey);
		}
	}

	protected void foreignKeyChanged_(ForeignKey foreignKey) {
		// do nothing by default
	}

	@Override
	protected void log(String flag, String message) {
		if (flag.equals(Tracing.UI_DB) && Tracing.booleanDebugOption(Tracing.UI_DB)) {
			this.log(message);
		} else {
			super.log(flag, message);
		}
	}


	// ********** connection listener **********

	protected class LocalConnectionListener implements ConnectionListener {

		protected LocalConnectionListener() {
			super();
		}

		public void opened(ConnectionProfile profile) {
			this.log("opened: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		public void modified(ConnectionProfile profile) {
			this.log("modified: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		public boolean okToClose(ConnectionProfile profile) {
			this.log("OK to close: " + profile.getName());
			return true;
		}

		public void aboutToClose(ConnectionProfile profile) {
			this.log("about to close: " + profile.getName());
		}

		public void closed(ConnectionProfile profile) {
			this.log("closed: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		public void databaseChanged(ConnectionProfile profile, Database database) {
			this.log("database changed: " + database.getName());
			DatabaseObjectCombo.this.databaseChanged(database);
		}

		public void catalogChanged(ConnectionProfile profile, Catalog catalog) {
			this.log("catalog changed: " + catalog.getName());
			DatabaseObjectCombo.this.catalogChanged(catalog);
		}

		public void schemaChanged(ConnectionProfile profile, Schema schema) {
			this.log("schema changed: " + schema.getName());
			DatabaseObjectCombo.this.schemaChanged(schema);
		}

		public void sequenceChanged(ConnectionProfile profile, Sequence sequence) {
			this.log("sequence changed: " + sequence.getName());
			DatabaseObjectCombo.this.sequenceChanged(sequence);
		}

		public void tableChanged(ConnectionProfile profile, Table table) {
			this.log("table changed: " + table.getName());
			DatabaseObjectCombo.this.tableChanged(table);
		}

		public void columnChanged(ConnectionProfile profile, Column column) {
			this.log("column changed: " + column.getName());
			DatabaseObjectCombo.this.columnChanged(column);
		}

		public void foreignKeyChanged(ConnectionProfile profile, ForeignKey foreignKey) {
			this.log("foreign key changed: " + foreignKey.getName());
			DatabaseObjectCombo.this.foreignKeyChanged(foreignKey);
		}

		protected void log(String message) {
			DatabaseObjectCombo.this.log(Tracing.UI_DB, message);
		}

	}
		
}

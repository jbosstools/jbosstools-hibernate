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

import org.eclipse.core.commands.util.Tracing;
import org.eclipse.jpt.common.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.common.ui.internal.widgets.ComboPane;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.common.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.JpaDataSource;
import org.eclipse.jpt.jpa.core.JpaNode;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.db.Catalog;
import org.eclipse.jpt.jpa.db.Column;
import org.eclipse.jpt.jpa.db.ConnectionListener;
import org.eclipse.jpt.jpa.db.ConnectionProfile;
import org.eclipse.jpt.jpa.db.Database;
import org.eclipse.jpt.jpa.db.ForeignKey;
import org.eclipse.jpt.jpa.db.Schema;
import org.eclipse.jpt.jpa.db.Sequence;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.jpt.jpa.ui.internal.details.db.CatalogCombo;
import org.eclipse.jpt.jpa.ui.internal.details.db.SchemaCombo;
import org.eclipse.jpt.jpa.ui.internal.details.db.SequenceCombo;
import org.eclipse.jpt.jpa.ui.internal.listeners.SWTConnectionListenerWrapper;
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
	extends ComboPane<T>
{
	/**
	 * The listener added to the <code>ConnectionProfile</code>.
	 * It keeps the combo in sync with the database metadata.
	 */
	private ConnectionListener connectionListener;
	
	private PropertyChangeListener connectionProfileListener;


	// ********** constructors **********

	protected DatabaseObjectCombo(
			Pane<? extends T> parentPane,
			Composite parent) {

		super(parentPane, parent);
	}

	protected DatabaseObjectCombo(
			Pane<?> parentPane,
			PropertyValueModel<? extends T> subjectHolder,
			Composite parent) {

		super(parentPane, subjectHolder, parent);
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

			@Override
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
	protected final Iterable<String> getValues() {
		return this.connectionProfileIsActive() ? this.getValues_() : IterableTools.<String>emptyIterable();
	}

	/**
	 * Called only when connection profile is active
	 */
	protected abstract Iterable<String> getValues_();


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

	protected final void databaseChanged(Database database) {
		if ( ! this.comboBox.isDisposed()) {
			this.databaseChanged_(database);
		}
	}

	protected void databaseChanged_(@SuppressWarnings("unused") Database database) {
		// do nothing by default
	}

	protected final void catalogChanged(Catalog catalog) {
		if ( ! this.comboBox.isDisposed()) {
			this.catalogChanged_(catalog);
		}
	}

	protected void catalogChanged_(@SuppressWarnings("unused") Catalog catalog) {
		// do nothing by default
	}

	protected final void schemaChanged(Schema schema) {
		if ( ! this.comboBox.isDisposed()) {
			this.schemaChanged_(schema);
		}
	}

	protected void schemaChanged_(@SuppressWarnings("unused") Schema schema) {
		// do nothing by default
	}

	protected final void sequenceChanged(Sequence sequence) {
		if ( ! this.comboBox.isDisposed()) {
			this.sequenceChanged_(sequence);
		}
	}

	protected void sequenceChanged_(@SuppressWarnings("unused") Sequence sequence) {
		// do nothing by default
	}

	protected final void tableChanged(Table table) {
		if ( ! this.comboBox.isDisposed()) {
			this.tableChanged_(table);
		}
	}

	protected void tableChanged_(@SuppressWarnings("unused") Table table) {
		// do nothing by default
	}

	protected final void columnChanged(Column column) {
		if ( ! this.comboBox.isDisposed()) {
			this.columnChanged_(column);
		}
	}

	protected void columnChanged_(@SuppressWarnings("unused") Column column) {
		// do nothing by default
	}

	protected final void foreignKeyChanged(ForeignKey foreignKey) {
		if ( ! this.comboBox.isDisposed()) {
			this.foreignKeyChanged_(foreignKey);
		}
	}

	protected void foreignKeyChanged_(@SuppressWarnings("unused") ForeignKey foreignKey) {
		// do nothing by default
	}

//	@Override
	protected void log(String flag, String message) {
		Tracing.printTrace(flag, message);
//		if (flag.equals(UI_DB) && booleanDebugOption(UI_DB, false)) {
//			this.log(message);
//		} else {
//			super.log(flag, message);
//		}
	}

	// broaden accessibility a bit
	@Override
	protected void repopulateComboBox() {
		super.repopulateComboBox();
	}


	// ********** connection listener **********

	protected class LocalConnectionListener
		implements ConnectionListener
	{
		protected LocalConnectionListener() {
			super();
		}

		@Override
		public void opened(ConnectionProfile profile) {
			this.log("opened: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		@Override
		public void modified(ConnectionProfile profile) {
			this.log("modified: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		@Override
		public boolean okToClose(ConnectionProfile profile) {
			this.log("OK to close: " + profile.getName());
			return true;
		}

		@Override
		public void aboutToClose(ConnectionProfile profile) {
			this.log("about to close: " + profile.getName());
		}

		@Override
		public void closed(ConnectionProfile profile) {
			this.log("closed: " + profile.getName());
			DatabaseObjectCombo.this.repopulateComboBox();
		}

		@Override
		public void databaseChanged(ConnectionProfile profile, Database database) {
			this.log("database changed: " + database.getName());
			DatabaseObjectCombo.this.databaseChanged(database);
		}

		@Override
		public void catalogChanged(ConnectionProfile profile, Catalog catalog) {
			this.log("catalog changed: " + catalog.getName());
			DatabaseObjectCombo.this.catalogChanged(catalog);
		}

		@Override
		public void schemaChanged(ConnectionProfile profile, Schema schema) {
			this.log("schema changed: " + schema.getName());
			DatabaseObjectCombo.this.schemaChanged(schema);
		}

		@Override
		public void sequenceChanged(ConnectionProfile profile, Sequence sequence) {
			this.log("sequence changed: " + sequence.getName());
			DatabaseObjectCombo.this.sequenceChanged(sequence);
		}

		@Override
		public void tableChanged(ConnectionProfile profile, Table table) {
			this.log("table changed: " + table.getName());
			DatabaseObjectCombo.this.tableChanged(table);
		}

		@Override
		public void columnChanged(ConnectionProfile profile, Column column) {
			this.log("column changed: " + column.getName());
			DatabaseObjectCombo.this.columnChanged(column);
		}

		@Override
		public void foreignKeyChanged(ConnectionProfile profile, ForeignKey foreignKey) {
			this.log("foreign key changed: " + foreignKey.getName());
			DatabaseObjectCombo.this.foreignKeyChanged(foreignKey);
		}

		protected void log(String message) {
			DatabaseObjectCombo.this.log(UI_DB, message);
		}
	}

	public static final String UI_DB = "/debug/ui/db";

}


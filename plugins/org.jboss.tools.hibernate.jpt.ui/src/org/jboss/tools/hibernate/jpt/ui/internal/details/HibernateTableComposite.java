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
package org.jboss.tools.hibernate.jpt.ui.internal.details;

import java.util.Collection;

import javax.persistence.Table;

import org.eclipse.jpt.common.ui.internal.util.PaneVisibilityEnabler;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.db.Schema;
import org.eclipse.jpt.jpa.db.SchemaContainer;
import org.eclipse.jpt.jpa.ui.details.JptJpaUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.db.CatalogCombo;
import org.eclipse.jpt.jpa.ui.internal.details.db.SchemaCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateTable;
import org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl.TableCombo;

/**
 * @author Dmitry Geraskov
 *
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * |                                                                           |
 * | - Table ----------------------------------------------------------------- |
 * | |          ------------------------------------------------------------ | |
 * | | Table:   | TableCombo                                               | | |
 * | |          ------------------------------------------------------------ | |
 * | |          ------------------------------------------------------------ | |
 * | | Catalog: | CatalogCombo                                             | | |
 * | |          ------------------------------------------------------------ | |
 * | |          ------------------------------------------------------------ | |
 * | | Schema:  | SchemaCombo                                              | | |
 * | |          ------------------------------------------------------------ | |
 * | ------------------------------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see Table
 * @see EntityComposite - The parent container
 * @see TableCombo
 * @see CatalogCombo
 * @see SchemaCombo
 *
 * @TODO repopulate this panel based on the Entity table changing
 *
 * @version 2.0
 * @since 1.0
 */
public class HibernateTableComposite extends Pane<HibernateEntity>
{
	
	/**
	 * Creates a new <code>HibernateTableComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject
	 * @param parent The parent container
	 */
	public HibernateTableComposite(Pane<? extends HibernateEntity> parentPane,
	                      Composite parent) {

		super(parentPane, parent);
	}
	
	@Override
	protected void initializeLayout(Composite container) {

		// Table group pane
		Group tableGroupPane = addTitledGroup(
			container,
			JptJpaUiDetailsMessages.TableComposite_tableSection
		);

		PropertyValueModel<HibernateTable> subjectHolder = buildTableHolder();
		
		// Table widgets
		this.addLabel(tableGroupPane, JptJpaUiDetailsMessages.TableChooser_label);
		this.addTableCombo(subjectHolder, tableGroupPane);		
//		addLabeledComposite(
//				tableGroupPane,
//				JptUiDetailsMessages.TableChooser_label,
//				addTableCombo(subjectHolder, tableGroupPane),
//				JpaHelpContextIds.ENTITY_TABLE
//			);
		
		// Catalog widgets
		this.addLabel(tableGroupPane, JptJpaUiDetailsMessages.CatalogChooser_label);
		this.addTableCombo(subjectHolder, tableGroupPane);
//		addLabeledComposite(
//			tableGroupPane,
//			JptUiDetailsMessages.CatalogChooser_label,
//			addCatalogCombo(subjectHolder, tableGroupPane),
//			JpaHelpContextIds.ENTITY_CATALOG
//		);

		// Schema widgets
		this.addLabel(tableGroupPane, JptJpaUiDetailsMessages.SchemaChooser_label);
		this.addTableCombo(subjectHolder, tableGroupPane);
//		addLabeledComposite(
//			tableGroupPane,
//			JptUiDetailsMessages.SchemaChooser_label,
//			addSchemaCombo(subjectHolder, tableGroupPane),
//			JpaHelpContextIds.ENTITY_SCHEMA
//		);
		
		new PaneVisibilityEnabler(buildTableEnabledHolder(), this);
	}
	
	protected ModifiablePropertyValueModel<HibernateTable> buildTableHolder() {
		
		return new PropertyAspectAdapter<HibernateEntity, HibernateTable>(getSubjectHolder(), HibernateEntity.TABLE_IS_UNDEFINED_PROPERTY) {
			@Override
			protected HibernateTable buildValue_() {
				return this.subject.tableIsUndefined() ? null : this.subject.getTable();
			}
		};
	}
	
	protected ModifiablePropertyValueModel<Boolean> buildTableEnabledHolder() {
		return new PropertyAspectAdapter<HibernateEntity, Boolean>(getSubjectHolder(), HibernateEntity.SPECIFIED_TABLE_IS_ALLOWED_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject.specifiedTableIsAllowed());
			}
		};
	}

	private CatalogCombo<HibernateTable> addCatalogCombo(PropertyValueModel<HibernateTable> tableHolder, Composite container) {

		return new CatalogCombo<HibernateTable>(this, tableHolder, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(HibernateTable.DEFAULT_CATALOG_PROPERTY);
				propertyNames.add(HibernateTable.SPECIFIED_CATALOG_PROPERTY);
			}

			@Override
			protected String getDefaultValue() {
				return getSubject().getDefaultCatalog();
			}

			@Override
			protected void setValue(String value) {
				getSubject().setSpecifiedCatalog(value);
			}

			@Override
			protected String getValue() {
				return getSubject().getSpecifiedCatalog();
			}
		};
	}

	private SchemaCombo<HibernateTable> addSchemaCombo(PropertyValueModel<HibernateTable> subjectHolder, Composite container) {

		return new SchemaCombo<HibernateTable>(this, subjectHolder, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(HibernateTable.DEFAULT_SCHEMA_PROPERTY);
				propertyNames.add(HibernateTable.SPECIFIED_SCHEMA_PROPERTY);
			}

			@Override
			protected String getDefaultValue() {
				return getSubject().getDefaultSchema();
			}

			@Override
			protected void setValue(String value) {
				getSubject().setSpecifiedSchema(value);
			}

			@Override
			protected String getValue() {
				return getSubject().getSpecifiedSchema();
			}

			@Override
			protected SchemaContainer getDbSchemaContainer_() {
				return this.getSubject().getDbSchemaContainer();
			}
		};
	}

	private TableCombo<HibernateTable> addTableCombo(PropertyValueModel<HibernateTable> subjectHolder, Composite container) {

		return new TableCombo<HibernateTable>(this, subjectHolder, container) {
			
			@Override
			protected void initializeLayout(Composite container) {
				super.initializeLayout(container);
				comboBox.addFocusListener(new FocusListener() {
					
					public void focusGained(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getSubject().getName());
							setPopulating(false);
						}						
					}
					
					public void focusLost(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getValue());
							setPopulating(false);
						}												
					}
				});
			}
			
			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(HibernateTable.DEFAULT_NAME_PROPERTY);
				propertyNames.add(HibernateTable.SPECIFIED_NAME_PROPERTY);
				propertyNames.add(HibernateTable.DEFAULT_SCHEMA_PROPERTY);
				propertyNames.add(HibernateTable.SPECIFIED_SCHEMA_PROPERTY);
				propertyNames.add(HibernateTable.DEFAULT_CATALOG_PROPERTY);
				propertyNames.add(HibernateTable.SPECIFIED_CATALOG_PROPERTY);
			}

			@Override
			protected void propertyChanged(String propertyName) {
				super.propertyChanged(propertyName);
				if (propertyName == HibernateTable.DEFAULT_SCHEMA_PROPERTY 
					|| propertyName == HibernateTable.SPECIFIED_SCHEMA_PROPERTY
					|| propertyName == HibernateTable.DEFAULT_CATALOG_PROPERTY
					|| propertyName == HibernateTable.SPECIFIED_CATALOG_PROPERTY ) {
					repopulate();
				}
			}
			
			@Override
			protected String getDefaultValue() {
				return this.getSubject().getDefaultDBTableName();
			}

			@Override
			protected void setValue(String value) {
				this.getSubject().setSpecifiedName(value);
			}

			@Override
			public String getValue() {
				String specifiedName = this.getSubject().getSpecifiedName();
				if (specifiedName == null){
					return null;
				}
				String dbTableName = this.getSubject().getDBTableName();
				if (specifiedName.equals(dbTableName)){
					return specifiedName;
				} else {
					return specifiedName + " (" + dbTableName +")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			@Override
			protected Schema getDbSchema_() {
				return this.getSubject().getDbSchema();
			}
			
		};
	}

}

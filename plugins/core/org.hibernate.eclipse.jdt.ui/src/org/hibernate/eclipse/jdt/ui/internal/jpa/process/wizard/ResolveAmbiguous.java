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
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.OwnerType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefFieldInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;

/**
 * Resolve ambiguous list wizard page
 *
 * @author Vitali
 */
public class ResolveAmbiguous extends UserInputWizardPage {

	protected IHibernateJPAWizardData data;

	protected IHibernateJPAWizardParams params;

	protected final int COLUMN_CLASS = 0;
	protected final int COLUMN_PROPERTY = 1;
	protected final int COLUMN_TYPE = 2;
	protected final int COLUMN_RELATED = 3;
	protected final int COLUMN_OWNER = 4;

	protected Table table;
	protected TableEditor editorType;
	protected TableEditor editorRel;
	protected TableEditor editorOwner;
	
	protected EditorTypeModifyListener editorTypeModifyListener = new EditorTypeModifyListener();
	protected EditorRelModifyListener editorRelModifyListener = new EditorRelModifyListener();
	protected EditorOwnerModifyListener editorOwnerModifyListener = new EditorOwnerModifyListener();
	
	public ResolveAmbiguous(String name, IHibernateJPAWizardData data, IHibernateJPAWizardParams params) {
		super(name);
		this.data = data;
		this.params = params;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        Label label = new Label(container, SWT.NULL);
        label.setText(JdtUiMessages.ResolveAmbiguous_message);
        table = new Table(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
		createTableColumns(table);
		TableItem ti = null;
		Iterator<Map.Entry<String, EntityInfo>> it = data.getEntities().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			if (entry.getValue().isAbstractFlag()) {
				continue;
			}
			//collectModification(bufferManager, entry.getKey(), entry.getValue());
			Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
				entry.getValue().getReferences().entrySet().iterator();
			while (referencesIt.hasNext()) {
				Map.Entry<String, RefEntityInfo> entryRef = referencesIt.next();
				RefEntityInfo rei = entryRef.getValue();
				ti = new TableItem(table, SWT.NONE);
				ti.setData(rei);
				ti.setText(COLUMN_CLASS, entry.getKey());
				String shortName = getShortName(rei.fullyQualifiedName);
				ti.setText(COLUMN_PROPERTY, shortName + " " + entryRef.getKey()); //$NON-NLS-1$
				ti.setText(COLUMN_TYPE, Utils.refTypeToStr(rei.refType));
				if (null != rei.mappedBy) {
					ti.setText(COLUMN_RELATED, rei.mappedBy);
				}
				else {
					ti.setText(COLUMN_RELATED, JdtUiMessages.ResolveAmbiguous_empty);
				}
				ti.setText(COLUMN_OWNER, Utils.ownerTypeToStr(rei.owner));
			}
		}
		//
		editorType = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editorType.horizontalAlignment = SWT.LEFT;
		editorType.grabHorizontal = true;
		editorType.minimumWidth = 50;
		//
		editorRel = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editorRel.horizontalAlignment = SWT.LEFT;
		editorRel.grabHorizontal = true;
		editorRel.minimumWidth = 50;
		//
		editorOwner = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editorOwner.horizontalAlignment = SWT.LEFT;
		editorOwner.grabHorizontal = true;
		editorOwner.minimumWidth = 50;
		// editing the second column
		table.addSelectionListener(new TableSelectionListener());
		GridData data = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		data.heightHint = convertHeightInCharsToPixels(10);
		table.setLayoutData(data);
		setControl(container);
	}

	protected class EditorTypeModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			CCombo text = (CCombo)editorType.getEditor();
			String str = text.getText();
			//editorType.getItem().setText(COLUMN_TYPE, str);
			TableItem ti = editorType.getItem();
			RefEntityInfo rei = (RefEntityInfo)ti.getData();
			RefType oldVal = rei.refType;
			OwnerType oldVal2 = rei.owner;
			rei.refType = Utils.strToRefType(str);
			if (rei.refType == RefType.ONE2MANY) {
				rei.owner = OwnerType.YES;
			}
			else if (rei.refType == RefType.MANY2ONE) {
				rei.owner = OwnerType.NO;
			}
			if (oldVal != rei.refType || oldVal2 != rei.owner) {
				RefEntityInfo rei2 = findMappedRefEntityInfo(rei);
				// firstly search
				TableItem ti2 = findTableItem(rei2, rei);
				// then - update
				updateDependentRefEntityInfoType(rei, rei2);
				updateEditorOwner(ti);
				updateTI(ti);
				updateTI(ti2);
				params.reCollectModification(data.getEntities());
			}
		}
	}

	public void updateDependentRefEntityInfoType(RefEntityInfo rei, RefEntityInfo rei2) {
		if (rei == null) {
			return;
		}
		if (rei.refType == RefType.MANY2ONE) {
			rei.owner = OwnerType.NO;
			if (rei2 != null) {
				rei2.refType = RefType.ONE2MANY;
				rei2.owner = OwnerType.YES;
			}
		}
		if (rei.refType == RefType.ONE2MANY) {
			rei.owner = OwnerType.YES;
			if (rei2 != null) {
				rei2.refType = RefType.MANY2ONE;
				rei2.owner = OwnerType.NO;
			}
		}
	}

	public void updateEditorType(TableItem item) {
		// Clean up any previous editor control
		Control oldEditorType = editorType.getEditor();
		if (oldEditorType != null) {
			oldEditorType.dispose();
		}
		// The control that will be the editor must be a child of the Table
		CCombo comboType = new CCombo(table, SWT.NONE);
		comboType.setEditable(false);
		Color bkgnd = table.getBackground();
		comboType.setBackground(bkgnd);
		RefEntityInfo rei = (RefEntityInfo)item.getData();
		comboType.add(Utils.refTypeToStr(RefType.ONE2ONE));
		comboType.add(Utils.refTypeToStr(RefType.ONE2MANY));
		comboType.add(Utils.refTypeToStr(RefType.MANY2ONE));
		comboType.add(Utils.refTypeToStr(RefType.MANY2MANY));
		comboType.add(Utils.refTypeToStr(RefType.UNDEF));
		comboType.setText(Utils.refTypeToStr(rei.refType));
		comboType.addModifyListener(editorTypeModifyListener);
		//comboType.selectAll();
		comboType.setFocus();
		editorType.setEditor(comboType, item, COLUMN_TYPE);
	}

	protected class EditorRelModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			CCombo text = (CCombo)editorRel.getEditor();
			String str = text.getText();
			//editorRel.getItem().setText(COLUMN_RELATED, str);
			TableItem ti = editorRel.getItem();
			RefEntityInfo rei = (RefEntityInfo)ti.getData();
			String oldVal = rei.mappedBy;
			if (JdtUiMessages.ResolveAmbiguous_empty.equals(str)) {
				rei.mappedBy = null;
			}
			else {
				rei.mappedBy = str;
			}
			if (oldVal != rei.mappedBy) {
				rei.refType = RefType.UNDEF;
				rei.owner = OwnerType.UNDEF;
				//RefEntityInfo rei2 = findMappedRefEntityInfo(rei);
				// firstly search
				//TableItem ti2 = findTableItem(rei2, rei);
				// then - update
				updateEditorType(ti);
				updateEditorOwner(ti);
				updateTI(ti);
				params.reCollectModification(data.getEntities());
			}
		}
	}

	public void updateEditorRel(TableItem item) {
		// Clean up any previous editor control
		Control oldEditorRel = editorRel.getEditor();
		if (oldEditorRel != null) {
			oldEditorRel.dispose();
		}
		CCombo comboRel = new CCombo(table, SWT.NONE);
		comboRel.setEditable(false);
		Color bkgnd = table.getBackground();
		comboRel.setBackground(bkgnd);
		comboRel.add(JdtUiMessages.ResolveAmbiguous_empty);
		String fullyQualifiedName = item.getText(0);
		RefEntityInfo rei = (RefEntityInfo)item.getData();
		Set<RefFieldInfo> setRefEntityInfo = findRelatedRefFieldInfos(fullyQualifiedName, rei);
		Iterator<RefFieldInfo> itTmp = setRefEntityInfo.iterator();
		while (itTmp.hasNext()) {
			RefFieldInfo rfi = itTmp.next();
			comboRel.add(rfi.fieldId);
		}
		if (null != rei.mappedBy) {
			comboRel.setText(rei.mappedBy);
		}
		else {
			comboRel.setText(JdtUiMessages.ResolveAmbiguous_empty);
		}
		comboRel.addModifyListener(editorRelModifyListener);
		//comboRel.selectAll();
		//comboRel.setFocus();
		editorRel.setEditor(comboRel, item, COLUMN_RELATED);
	}

	protected class EditorOwnerModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			CCombo text = (CCombo)editorOwner.getEditor();
			String str = text.getText();
			//editorOwner.getItem().setText(COLUMN_OWNER, str);
			TableItem ti = editorOwner.getItem();
			RefEntityInfo rei = (RefEntityInfo)ti.getData();
			OwnerType oldVal = rei.owner;
			rei.owner = Utils.strToOwnerType(str);
			if (oldVal != rei.owner) {
				RefEntityInfo rei2 = findMappedRefEntityInfo(rei);
				// firstly search
				TableItem ti2 = findTableItem(rei2, rei);
				// then - update
				updateDependentRefEntityInfoOwner(rei, rei2);
				updateEditorType(ti);
				updateTI(ti);
				updateTI(ti2);
				params.reCollectModification(data.getEntities());
			}
		}
	}

	public void updateDependentRefEntityInfoOwner(RefEntityInfo rei, RefEntityInfo rei2) {
		if (rei == null) {
			return;
		}
		if (rei2 != null) {
			if (rei.refType == RefType.ONE2ONE || rei.refType == RefType.MANY2MANY) {
				if (rei.owner == OwnerType.YES) {
					rei2.owner = OwnerType.NO;
				} else if (rei.owner == OwnerType.NO) {
					rei2.owner = OwnerType.YES;
				}
			}
		}
		if (rei.refType == RefType.MANY2ONE) {
			if (rei.owner == OwnerType.YES) {
				rei.refType = RefType.UNDEF;
				if (rei2 != null) {
					rei2.refType = RefType.UNDEF;
					rei2.owner = OwnerType.NO;
				}
			}
			else if (rei2 != null) {
				rei2.owner = OwnerType.YES;
			}
		}
		if (rei.refType == RefType.ONE2MANY) {
			if (rei.owner == OwnerType.NO) {
				rei.refType = RefType.UNDEF;
				if (rei2 != null) {
					rei2.refType = RefType.UNDEF;
					rei2.owner = OwnerType.YES;
				}
			}
			else if (rei2 != null) {
				rei2.owner = OwnerType.NO;
			}
		}
	}

	public void updateEditorOwner(TableItem item) {
		// Clean up any previous editor control
		Control oldEditorOwner = editorOwner.getEditor();
		if (oldEditorOwner != null) {
			oldEditorOwner.dispose();
		}
		RefEntityInfo rei = (RefEntityInfo)item.getData();
		CCombo comboOwner = new CCombo(table, SWT.NONE);
		comboOwner.setEditable(false);
		Color bkgnd = table.getBackground();
		comboOwner.setBackground(bkgnd);
		comboOwner.add(Utils.ownerTypeToStr(OwnerType.YES));
		comboOwner.add(Utils.ownerTypeToStr(OwnerType.NO));
		comboOwner.setText(Utils.ownerTypeToStr(rei.owner));
		comboOwner.addModifyListener(editorOwnerModifyListener);
		editorOwner.setEditor(comboOwner, item, COLUMN_OWNER);
	}

	protected class TableSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			// Identify the selected row
			TableItem item = (TableItem)e.item;
			if (item == null) {
				return;
			}
			updateEditorType(item);
			updateEditorRel(item);
			updateEditorOwner(item);
		}
	}

	public void updateTI(TableItem ti) {
		if (ti == null) {
			return;
		}
		RefEntityInfo rei = (RefEntityInfo)ti.getData();
		ti.setText(COLUMN_TYPE, Utils.refTypeToStr(rei.refType));
		if (null != rei.mappedBy) {
			ti.setText(COLUMN_RELATED, rei.mappedBy);
		}
		else {
			ti.setText(COLUMN_RELATED, JdtUiMessages.ResolveAmbiguous_empty);
		}
		ti.setText(COLUMN_OWNER, Utils.ownerTypeToStr(rei.owner));
	}

	protected void createTableColumns(Table table) {
		TableColumn column = null;
		
		column = new TableColumn(table, SWT.LEFT, COLUMN_CLASS);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Class);
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, COLUMN_PROPERTY);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Property);
		column.setWidth(140);

		column = new TableColumn(table, SWT.LEFT, COLUMN_TYPE);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Type);
		column.setWidth(60);

		column = new TableColumn(table, SWT.LEFT, COLUMN_RELATED);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Related);
		column.setWidth(80);

		column = new TableColumn(table, SWT.LEFT, COLUMN_OWNER);
		column.setText(JdtUiMessages.ResolveAmbiguous_column_Owner);
		column.setWidth(50);
	}

	public String getShortName(String fullyQualifiedName) {
		int idx = fullyQualifiedName.lastIndexOf('.');
		if (idx == -1) {
			return fullyQualifiedName;
		}
		return fullyQualifiedName.substring(idx + 1);
	}
	
	protected Set<RefFieldInfo> findRelatedRefFieldInfos(
			String fullyQualifiedName, RefEntityInfo rei) {
		String fullyQualifiedName2 = rei.fullyQualifiedName;
		EntityInfo entryInfo2 = data.getEntities().get(fullyQualifiedName2);
		Set<RefFieldInfo> setRefEntityInfo = entryInfo2.getRefFieldInfoSet(fullyQualifiedName);
		return setRefEntityInfo;
	}
	
	protected RefEntityInfo findMappedRefEntityInfo(RefEntityInfo rei) {
		if (rei.mappedBy == null) {
			return null;
		}
		String fullyQualifiedName2 = rei.fullyQualifiedName;
		EntityInfo entryInfo2 = data.getEntities().get(fullyQualifiedName2);
		RefEntityInfo refEntityInfo = entryInfo2.getFieldIdRefEntityInfo(rei.mappedBy);
		return refEntityInfo;
	}

	public String getFieldId(String fieldId) {
		int idx = fieldId.lastIndexOf(' ');
		if (idx == -1) {
			return fieldId;
		}
		return fieldId.substring(idx + 1);
	}
	
	protected TableItem findTableItem(RefEntityInfo rei1, RefEntityInfo rei2) {
		if (rei1 == null || rei2 == null ) {
			return null;
		}
		TableItem ti = null, tiRes = null;
		TableItem[] tis = table.getItems();
		for (int i = 0; i < tis.length; i++) {
			ti = tis[i];
			if (!ti.getText(COLUMN_CLASS).equals(rei2.fullyQualifiedName)) {
				continue;
			}
			String fieldId1 = getFieldId(ti.getText(COLUMN_PROPERTY));
			String fieldId2 = ti.getText(COLUMN_RELATED);
			if (fieldId2.equals(rei1.mappedBy) && fieldId1.equals(rei2.mappedBy)) {
				tiRes = ti;
				break;
			}
		}
		return tiRes;
	}
}

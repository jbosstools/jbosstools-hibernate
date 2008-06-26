/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.hibernate.internal.core.properties.xpl;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
;

/**
 * @author alex
 *
 * Cell editor with editable combo box
 */
public class EditableListCellEditor extends CellEditor {
    /**
     * The list of items to present in the combo box.
     */
    private String[] items;

    /**
     * The custom combo box control.
     */
    CCombo comboBox;

    /**
     * Default ComboBoxCellEditor style
     */
    private static final int defaultStyle = SWT.NONE;

    /**
     * Creates a new cell editor with no control and no  st of choices. Initially,
     * the cell editor has no cell validator.
     * 
     * @since 2.1
     * @see #setStyle
     * @see #create
     * @see #setItems
     * @see #dispose
     */
    public EditableListCellEditor() {
        setStyle(defaultStyle);
    }

    /**
     * Creates a new cell editor with a combo containing the given 
     * list of choices and parented under the given control. The cell
     * editor value is the zero-based index of the selected item.
     * Initially, the cell editor has no cell validator and
     * the first item in the list is selected. 
     *
     * @param parent the parent control
     * @param items the list of strings for the combo box
     */
    public EditableListCellEditor(Composite parent, String[] items) {
        this(parent, items, defaultStyle);
    }

    /**
     * Creates a new cell editor with a combo containing the given 
     * list of choices and parented under the given control. The cell
     * editor value is the zero-based index of the selected item.
     * Initially, the cell editor has no cell validator and
     * the first item in the list is selected. 
     *
     * @param parent the parent control
     * @param items the list of strings for the combo box
     * @param style the style bits
     * @since 2.1
     */
    public EditableListCellEditor(Composite parent, String[] items, int style) {
        super(parent, style);
        setItems(items);
    }

    /**
     * Returns the list of choices for the combo box
     *
     * @return the list of choices for the combo box
     */
    public String[] getItems() {
        return this.items;
    }

    /**
     * Sets the list of choices for the combo box
     *
     * @param items the list of choices for the combo box
     */
    public void setItems(String[] items) {
        this.items = items;
        populateComboBoxItems();
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected Control createControl(Composite parent) {

        comboBox = new CCombo(parent, getStyle());
        comboBox.setFont(parent.getFont());

        comboBox.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        comboBox.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            public void widgetSelected(SelectionEvent event) {
            }
        });

        comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
            	EditableListCellEditor.this.focusLost();
            }
        });
        return comboBox;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the zero-based index of the current selection.
     *
     * @return the zero-based index of the current selection wrapped
     *  as an <code>Integer</code>
     */
    protected Object doGetValue() {
        return comboBox.getText();
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
        comboBox.setFocus();
    }

    /**
     * The <code>EditableListCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method sets the 
     * minimum width of the cell.  The minimum width is 10 characters
     * if <code>comboBox</code> is not <code>null</code> or <code>disposed</code>
     * eles it is 60 pixels to make sure the arrow button and some text is visible.
     * The list of CCombo will be wide enough to show its longest item.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((comboBox == null) || comboBox.isDisposed())
            layoutData.minimumWidth = 60;
        else {
            // make the comboBox 10 characters wide
            GC gc = new GC(comboBox);
            layoutData.minimumWidth = (gc.getFontMetrics()
                    .getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method
     * accepts a zero-based index of a selection.
     *
     * @param value the zero-based index of the selection wrapped
     *   as an <code>Integer</code>
     */
    protected void doSetValue(Object value) {
        comboBox.setText((String)value);
    }

    /**
     * Updates the list of choices for the combo box for the current control.
     */
    private void populateComboBoxItems() {
        if (comboBox != null && items != null) {
            comboBox.removeAll();
            for (int i = 0; i < items.length; i++)
                comboBox.add(items[i], i);

            setValueValid(true);
        }
    }

    /**
     * Applies the currently selected value and deactiavates the cell editor
     */
    void applyEditorValueAndDeactivate() {
        //	must set the selection before getting value
        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);
        if (!isValid) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(),
                    new Object[] { comboBox.getText() }));
        }
        fireApplyEditorValue();
        deactivate();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#focusLost()
     */
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        } else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }

}

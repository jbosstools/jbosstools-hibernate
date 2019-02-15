/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.views;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.hibernate.console.StringListDialog;

/**
 * @author Dmitry Geraskov
 *
 */
public class StringArrayDialogCellEditor extends TextCellEditor {
	
	private Text text;
	
    /**
     * The button.
     */
    private Button button;
	
	/**
     * The editor control.
     */
    private Composite editor;
    
    private String[] strValues = null;
    
    private FocusListener buttonFocusListener;
    
    protected StringArrayDialogCellEditor(Composite parent) {
        super(parent);
    }

	@Override
	protected Control createControl(Composite parent) {
		Font font = parent.getFont();
        Color bg = parent.getBackground();

        editor = new Composite(parent, getStyle());
        editor.setFont(font);
        editor.setBackground(bg);
        editor.setLayout(new DialogCellLayout());

        text = (Text) super.createControl(editor);
        updateContents(strValues);

        button = createButton(editor);
        button.setFont(font);

        button.addKeyListener(new KeyAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
             */
            public void keyReleased(KeyEvent e) {
                if (e.character == '\u001b') { // Escape
                    fireCancelEditor();
                }
            }
        });
        
        button.addFocusListener(getButtonFocusListener());
        
        button.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
            	// Remove the button's focus listener since it's guaranteed
            	// to lose focus when the dialog opens
            	button.removeFocusListener(getButtonFocusListener());
                
            	Object newValue = openDialogBox(editor);
            	
            	// Re-add the listener once the dialog closes
            	button.addFocusListener(getButtonFocusListener());

            	if (newValue != null) {
                    boolean newValidState = isCorrect(newValue);
                    if (newValidState) {
                        markDirty();
                        doSetValue(newValue);
                    } else {
                        // try to insert the current value into the error message.
                        setErrorMessage(MessageFormat.format(getErrorMessage(),
                                new Object[] { newValue.toString() }));
                    }
                    fireApplyEditorValue();
                }
            }

			
        });

        setValueValid(true);

        return editor;
	}
	
	@Override
	protected void focusLost() {
		if (isActivated()) {
			if (button != null && !button.isDisposed() && !button.isFocusControl()) {
	    		if (text != null && !text.isDisposed() && !text.isFocusControl()){
	    			fireApplyEditorValue();
	    			deactivate();
	    		}
	    	}
		}
	}
	
	protected Object openDialogBox(Control cellEditorWindow) {
		String[] value = (String[]) getValue();
		StringListDialog pld = new StringListDialog(null, value);
		if (pld.open()==Window.OK){
			return pld.getValue();//String[]
		}
		return null;
	}
	
    private FocusListener getButtonFocusListener() {
    	if (buttonFocusListener == null) {
    		buttonFocusListener = new FocusListener() {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
				 */
				public void focusGained(FocusEvent e) {
					
				}

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
				 */
				public void focusLost(FocusEvent e) {
					StringArrayDialogCellEditor.this.focusLost();
				}
    		};
    	}
    	
    	return buttonFocusListener;
	}

    protected Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText("..."); //$NON-NLS-1$
        return result;
    }
    
    public void deactivate() {
    	if (button != null && !button.isDisposed()) {
    		button.removeFocusListener(getButtonFocusListener());
    	}
    	
		super.deactivate();
	}
    
    @Override
    protected void editOccured(ModifyEvent e) {
    	String text = this.text.getText();
    	if (strValues == null || strValues.length == 0){
    		strValues = new String[]{text};
    	} else {
    		strValues[0] = text;
    	}
    	super.editOccured(e);
    }
    
    protected Object doGetValue() {
        return strValues;
    }

    protected void doSetValue(Object value) {    	
    	if (value == null) {
    		this.strValues = new String[]{""}; //$NON-NLS-1$
    	} else if (value.getClass().isArray()){
    		this.strValues = (String[])value;
    	}
    	super.doSetValue(strValues.length > 0 ? strValues[0] : ""); //$NON-NLS-1$
    }

	protected void updateContents(String[] value) {
        if (text == null) {
			return;
		}

        String strVal = "";//$NON-NLS-1$
        if (value != null && value.length > 0) {
        	strVal = value[0];
		}
        text.setText(strVal);
    }

	private class DialogCellLayout extends Layout {
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (text != null) {
            	text.setBounds(0, 0, bounds.width - size.x, bounds.height);
			}
            button.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
        }

        public Point computeSize(Composite editor, int wHint, int hHint,
                boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
            Point contentsSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                    force);
            Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                    force);
            // Just return the button width to ensure the button is not clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y,
                    buttonSize.y));
            return result;
        }
    }

	
}
/*******************************************************************************
 * Copyright (c) 2000, 2005, 2006 IBM Corporation, JBoss Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Max Rydahl Andersen, JBoss Inc. - added tooltip description support
 *******************************************************************************/

package org.hibernate.eclipse.mapper.editors.reveng.xpl;

import java.text.BreakIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.hibernate.eclipse.mapper.editors.reveng.IFormTextEntryListener;

/**
 * Helper to have a label/text entry field in a Form.
 * 
 * Supports hyperlinking of the label and optionally a button after the text.
 * 
 * From the eclipse forms internal package. The attached listener reacts to all
 * the events. Entring new text makes the entry 'dirty', but only when 'commit'
 * is called is 'valueChanged' method called (and only if 'dirty' flag is set).
 * This allows delayed commit.
 */
public class FormTextEntry {
	
	private static final int TOOLTIP_WIDTH_LIMIT = 300;
	private Control label;
	private Text text;
	private Button browse;
	private String value=""; //$NON-NLS-1$
	private String description;
	private boolean dirty;
	boolean ignoreModify = false;
	private IFormTextEntryListener listener;	
	
	/**
	 * The default constructor. Call 'createControl' to make it.
	 *  
	 */
	public FormTextEntry(Composite parent, FormToolkit toolkit, String labelText, int style) {
		createControl(parent, toolkit, labelText, style, null, false, 0);
	}
	
	/**
	 * This constructor create all the controls right away.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param labelText
	 * @param browseText
	 * @param linkLabel
	 */
	public FormTextEntry(Composite parent, FormToolkit toolkit, String labelText,
			String browseText, boolean linkLabel) {
		this(parent, toolkit, labelText, browseText, linkLabel, 0);
	}
	
	public FormTextEntry(Composite parent, FormToolkit toolkit, String labelText,
			String browseText, boolean linkLabel, int indent) {
		createControl(parent, toolkit, labelText, SWT.SINGLE, browseText, linkLabel, indent);
	}
	
	/** all constructor */
	public FormTextEntry(Composite parent, FormToolkit toolkit, String labelText,
			int style, String browseText, boolean linkLabel, int indent) {
		createControl(parent, toolkit, labelText, style, browseText, linkLabel, indent);
	}
	
	/**
	 * Create all the controls in the provided parent.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param labelText
	 * @param span
	 * @param browseText
	 * @param linkLabel
	 */
	private void createControl(Composite parent, FormToolkit toolkit,
			String labelText, int style, String browseText, boolean linkLabel, int indent) {
		if (linkLabel) {
			Hyperlink link = toolkit.createHyperlink(parent, labelText,
					SWT.NULL);
			label = link;
		} else {
			label = toolkit.createLabel(parent, labelText);
			label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
			label.setToolTipText(getToolTipText(label));
		}
		text = toolkit.createText(parent, "", style); //$NON-NLS-1$
		addListeners();
		if (browseText != null) {
			browse = toolkit.createButton(parent, browseText, SWT.PUSH);
			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (listener != null)
						listener.browseButtonSelected(FormTextEntry.this);
				}
			});
		}
		fillIntoGrid(parent, indent);
	}
	public void setEditable(boolean editable) {
		text.setEditable(editable);
		if (browse!=null) 
			browse.setEnabled(editable);
	}
	
	public void setEnabled(boolean enabled) {
		
	}
	private void fillIntoGrid(Composite parent, int indent) {
		Layout layout = parent.getLayout();
		if (layout instanceof GridLayout) {
			GridData gd;
			int span = ((GridLayout) layout).numColumns;
			gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
			gd.horizontalIndent = indent;
			label.setLayoutData(gd);
			int tspan = browse != null ? span - 2 : span - 1;
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan = tspan;
			gd.grabExcessHorizontalSpace = (tspan == 1);
			gd.widthHint = 10;
			text.setLayoutData(gd);
			if (browse != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
				browse.setLayoutData(gd);
			}
		} else if (layout instanceof TableWrapLayout) {
			TableWrapData td;
			int span = ((TableWrapLayout) layout).numColumns;
			td = new TableWrapData();
			td.valign = TableWrapData.MIDDLE;
			td.indent = indent;
			label.setLayoutData(td);
			int tspan = browse != null ? span - 2 : span - 1;
			td = new TableWrapData(TableWrapData.FILL);
			td.colspan = tspan;
			td.grabHorizontal = (tspan == 1);
			td.valign = TableWrapData.MIDDLE;
			text.setLayoutData(td);
			if (browse != null) {
				td = new TableWrapData();
				td.valign = TableWrapData.MIDDLE;
				browse.setLayoutData(td);
			}
		}
	}
	/**
	 * Attaches the listener for the entry.
	 * 
	 * @param listener
	 */
	public void setFormEntryListener(IFormTextEntryListener listener) {
		if (label instanceof Hyperlink) {
			if (this.listener!=null)
				((Hyperlink)label).removeHyperlinkListener(this.listener);
			if (listener!=null)
				((Hyperlink)label).addHyperlinkListener(listener);
		}
		this.listener = listener;
	}
	private void addListeners() {
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				editOccured(e);
			}
		});
		text.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (listener != null)
					listener.focusGained(FormTextEntry.this);
			}
			public void focusLost(FocusEvent e) {
				if (dirty)
					commit();
			}
		});
	}
	/**
	 * If dirty, commits the text in the widget to the value and notifies the
	 * listener. This call clears the 'dirty' flag.
	 *  
	 */
	public void commit() {
		if (dirty) {
			value = text.getText();
			//if (value.length()==0)
				//value = null;
			//notify
			if (listener != null)
				listener.textValueChanged(this);
		}
		dirty = false;
	}
	public void cancelEdit() {
		dirty = false;
	}
	private void editOccured(ModifyEvent e) {
		if (ignoreModify)
			return;
		dirty = true;
		if (listener != null)
			listener.textDirty(this);
	}
	/**
	 * Returns the text control.
	 * 
	 * @return
	 */
	public Text getText() {
		return text;
	}
	
	/**
	 * Returns the browse button control.
	 * @return
	 */
	public Button getButton() {
		return browse;
	}
	/**
	 * Returns the current entry value. If the entry is dirty and was not
	 * commited, the value may be different from the text in the widget.
	 * 
	 * @return
	 */
	public String getValue() {
		return value.trim();
	}
	/**
	 * Returns true if the text has been modified.
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return dirty;
	}
	private void keyReleaseOccured(KeyEvent e) {
		if (e.character == '\r') {
			// commit value
			if (dirty)
				commit();
		} else if (e.character == '\u001b') { // Escape character
			text.setText(value != null ? value : ""); // restore old //$NON-NLS-1$
			dirty = false;
		}	
		listener.selectionChanged(FormTextEntry.this);
	}
	/**
	 * Sets the value of this entry.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (text != null)
			text.setText(value != null ? value : ""); //$NON-NLS-1$
		this.value = (value != null) ? value : ""; //$NON-NLS-1$
	}
	/**
	 * Sets the value of this entry with the possibility to turn the
	 * notification off.
	 * 
	 * @param value
	 * @param blockNotification
	 */
	public void setValue(String value, boolean blockNotification) {
		ignoreModify = blockNotification;
		setValue(value);
		ignoreModify = false;
	}
	
	protected String getToolTipText(Control control) {
		String text = getDescription();
		if (text==null) return null;
		int dot = text.indexOf('.');
		if (dot != -1) {
			StringBuffer buf = new StringBuffer();
			boolean inTag=false;
			for (int i=0; i<text.length(); i++) {
				char c = text.charAt(i);
				if (inTag) {
					if (c=='>') {
						inTag = false;
						continue;
					}
				}
				else {
					if (c=='<') {
						inTag = true;
						continue;
					}
					else if (c=='.') {
						if (i<text.length()-1) {
							char c2 = text.charAt(i+1);
							if (c2==' ' || c2=='\t' || c2=='\n') break;
						}
					}
					buf.append(c);
				}
			}
			return wrapText(control, buf.toString(), TOOLTIP_WIDTH_LIMIT);
		}
		return text;
	}
	
	private String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
		if(label!=null) {
			label.setToolTipText(getToolTipText(label));
		}
	}

	private String wrapText(Control c, String src, int width) {
		BreakIterator wb = BreakIterator.getWordInstance();
		wb.setText(src);
		int saved = 0;
		int last = 0;
		StringBuffer buff = new StringBuffer();
		GC gc = new GC(c);
		
		for (int loc = wb.first(); loc != BreakIterator.DONE; loc = wb.next()) {
			String word = src.substring(saved, loc);
			Point extent = gc.textExtent(word);
			if (extent.x > width) {
				// overflow
				String prevLine = src.substring(saved, last);
				buff.append(prevLine);
				buff.append(SWT.LF);
				saved = last;
			}
			last = loc;
		}
		String lastLine = src.substring(saved, last);
		buff.append(lastLine);
		return buff.toString();
	}

}

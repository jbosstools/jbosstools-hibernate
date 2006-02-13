package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.hibernate.eclipse.mapper.editors.reveng.xpl.FormTextEntry;

public interface IFormTextEntryListener extends IHyperlinkListener {
/**
 * The user clicked on the text control and focus was
 * transfered to it.
 * @param entry
 */
	void focusGained(FormTextEntry entry);
/**
 * The user changed the text in the text control of the entry.
 * @param entry
 */
	void textDirty(FormTextEntry entry);
/**
 * The value of the entry has been changed to be the text
 * in the text control (as a result of 'commit' action).
 * @param entry
 */
	void textValueChanged(FormTextEntry entry);
/**
 * The user pressed the 'Browse' button for the entry.
 * @param entry
 */
	void browseButtonSelected(FormTextEntry entry);
	
	void selectionChanged(FormTextEntry entry);
}

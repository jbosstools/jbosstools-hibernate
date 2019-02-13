/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

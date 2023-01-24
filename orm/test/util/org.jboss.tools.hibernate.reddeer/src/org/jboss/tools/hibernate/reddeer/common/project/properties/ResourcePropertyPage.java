/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.common.project.properties;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.reddeer.swt.api.Button;
import org.eclipse.reddeer.swt.api.Group;
import org.eclipse.reddeer.swt.impl.button.LabeledCheckBox;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;

public class ResourcePropertyPage extends PropertyPage {

	public static final String NAME = "Resource"; 
	public static final String TEXT_FILE_ENCODING_GROUP = "Text file encoding";
	public static final String TEXT_FILE_DELIMITER_GROUP = "New text file line delimiter";
	public static final String INHERITED_BUTTON = "Inherited from container (UTF-8)";
	public static final String OTHER_BUTTON = "Other:";
	public static final String STORE_SEPARATELY_BUTTON = "Store the encoding of derived resources separately";
	
	public ResourcePropertyPage(ReferencedComposite referencedComposite) {
		super(referencedComposite, NAME);
	}
	
	public Group getResourceGroup(String name) {
		return new DefaultGroup(name);
	}
	
	public Group getResourceEncodingGroup() {
		return getResourceGroup(TEXT_FILE_ENCODING_GROUP);
	}
	
	public Button getInherittedButton() {
		return new RadioButton(getResourceEncodingGroup(), INHERITED_BUTTON);
	}
	
	public Button getOtherButton() {
		return new RadioButton(getResourceEncodingGroup(), OTHER_BUTTON);
	}
	
	public Button getStoreEncodingButton() {
		return new LabeledCheckBox(getResourceEncodingGroup(), STORE_SEPARATELY_BUTTON);
	}
	
	public DefaultCombo getOtherEncodingCombo() {
		return new DefaultCombo(referencedComposite);
	}
	
	public void setOtherEncoding(String encoding) {
		getOtherButton().click();
		getOtherEncodingCombo().setSelection(encoding);
	}
	
	public void setInheritedEncoding() {
		getInherittedButton().click();
	}

}

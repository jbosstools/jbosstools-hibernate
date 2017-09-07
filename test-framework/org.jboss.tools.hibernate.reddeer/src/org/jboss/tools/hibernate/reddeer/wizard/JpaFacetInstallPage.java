/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.wizard;

import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.reference.ReferencedComposite;

/**
 * JPA Facets wizard page reddeer implementation
 * @author jpeterka
 * TOTO move to reddeer
 *
 */
public class JpaFacetInstallPage extends WizardPage {
	
	public JpaFacetInstallPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets JPA platform
	 * @param platform given platform
	 */
	public void setPlatform(String platform) {
		new DefaultCombo(referencedComposite, 0).setSelection(platform);
	}	

	/**
	 * sets JPA implementation 
	 * @param impl given implementation
	 */
	public void setJpaImplementation(String impl) {
		new DefaultCombo(referencedComposite, 1).setSelection(impl.toString());
	}

	/**
	 * Sets connection profile for JPA 
	 * @param profileName given connection profile
	 */
	public void setConnectionProfile(String profileName) {
		DefaultGroup group = new DefaultGroup(referencedComposite, "Connection");
		new WaitUntil(new ControlIsEnabled(new DefaultCombo(group)));
		new DefaultCombo(group).setSelection(profileName);
		PushButton apply = new PushButton(referencedComposite, "Apply");
		apply.click();
		new WaitWhile(new JobIsRunning());
		new WaitWhile(new ShellIsAvailable("Progress Information"), TimePeriod.LONG);
	}

	/**
	 * Set JPA autodiscovery
	 * @param autoDiscovery if set to true autodiscovery is set
	 */
	public void setAutoDiscovery(boolean autoDiscovery) {
		if (autoDiscovery) {
			new RadioButton(referencedComposite, "Discover annotated classes automatically").click();
		}
		
	}

}
